package de.partspicker.web.workflow.business

import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.workflow.EdgeEntityGenerators
import de.partspicker.web.test.generators.workflow.InstanceEntityGenerators
import de.partspicker.web.test.generators.workflow.NodeEntityGenerators
import de.partspicker.web.test.generators.workflow.WorkflowEntityGenerators
import de.partspicker.web.workflow.business.exceptions.NodeNotAdvanceableByUserRuleException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowStartedWithNonStartNodeException
import de.partspicker.web.workflow.business.objects.nodes.AutomatedActionNode
import de.partspicker.web.workflow.persistence.EdgeRepository
import de.partspicker.web.workflow.persistence.InstanceRepository
import de.partspicker.web.workflow.persistence.NodeRepository
import de.partspicker.web.workflow.persistence.WorkflowRepository
import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.util.Optional

class WorkflowInteractionServiceUnitTest : ShouldSpec({
    val workflowRepositoryMock = mockk<WorkflowRepository>()
    val instanceRepositoryMock = mockk<InstanceRepository>()
    val nodeRepositoryMock = mockk<NodeRepository>()
    val edgeRepositoryMock = mockk<EdgeRepository>()
    val instanceValueServiceMock = mockk<InstanceValueService>()
    val projectRepositoryMock = mockk<ProjectRepository>()

    val cut = WorkflowInteractionService(
        workflowRepository = workflowRepositoryMock,
        instanceRepository = instanceRepositoryMock,
        nodeRepository = nodeRepositoryMock,
        edgeRepository = edgeRepositoryMock,
        instanceValueService = instanceValueServiceMock,
        projectRepository = projectRepositoryMock
    )

    context("read instance info") {
        should("return instance info") {
            // given
            val currentNodeEntity = NodeEntityGenerators.userActionNodeEntityGenerator.single()
            val instanceEntity = InstanceEntityGenerators.generator.single().copy(
                currentNode = currentNodeEntity
            )
            val options = listOf(
                EdgeEntityGenerators.generator.single(),
                EdgeEntityGenerators.generator.single()
            )

            every { instanceRepositoryMock.findById(instanceEntity.id) } returns Optional.of(instanceEntity)
            every { edgeRepositoryMock.findAllBySourceId(currentNodeEntity.id) } returns options

            // when
            val returnedNodeInfo = cut.readInstanceInfo(instanceEntity.id)

            // then
            returnedNodeInfo.nodeId shouldBe instanceEntity.currentNode.id
            returnedNodeInfo.name shouldBe instanceEntity.currentNode.name
            returnedNodeInfo.instanceId shouldBe instanceEntity.id
            returnedNodeInfo.options shouldHaveSize options.size
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(1).next()
            every { instanceRepositoryMock.findById(randomId) } returns Optional.empty()

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.readInstanceInfo(randomId)
            }

            // then
            exception.message shouldBe "Workflow instance with id $randomId could not be found"
        }
    }

    context("start workflow instance") {
        should("throw WorkflowNameNotFoundException when given non-existent workflow name") {
            // given
            val nonExistentWorkflowName = "nonExistentName"
            every { workflowRepositoryMock.findLatest(nonExistentWorkflowName) } returns null

            // when
            val exception = shouldThrow<WorkflowNameNotFoundException> {
                cut.startWorkflowInstance(nonExistentWorkflowName, "someNode")
            }

            // then
            exception.message shouldBe "Workflow with name $nonExistentWorkflowName could not be found"
        }

        should("throw WorkflowNodeNameNotFoundException when given non-existent node name") {
            // given
            val workflow = WorkflowEntityGenerators.generator.single()
            val nonExistentNode = "nonExistentNode"
            every {
                workflowRepositoryMock.findLatest(workflow.name)
            } returns workflow
            every { nodeRepositoryMock.findByWorkflowIdAndName(workflow.id, nonExistentNode) } returns null

            // when
            val exception = shouldThrow<WorkflowNodeNameNotFoundException> {
                cut.startWorkflowInstance(workflow.name, nonExistentNode)
            }

            // then
            exception.message shouldBe "Workflow node with name $nonExistentNode " +
                "could not be found for workflow with name ${workflow.name}"
        }

        should("throw WorkflowStartedWithNonStartNodeException when given node name is not a start node") {
            // given
            val workflow = WorkflowEntityGenerators.generator.single()
            val nonStartNode = NodeEntityGenerators.userActionNodeEntityGenerator.single()
            every {
                workflowRepositoryMock.findLatest(workflow.name)
            } returns workflow
            every { nodeRepositoryMock.findByWorkflowIdAndName(workflow.id, nonStartNode.name) } returns nonStartNode

            // when
            val exception = shouldThrow<WorkflowStartedWithNonStartNodeException> {
                cut.startWorkflowInstance(workflow.name, nonStartNode.name)
            }

            // then
            exception.message shouldBe "Workflow can only be started at a start node. The node with name " +
                "${nonStartNode.name} is not a start node of the workflow with name ${workflow.name}"
        }
    }

    context("advanceInstanceNodeByUser") {
        should("call advanceInstanceNodeBySystem when given current node with type user action") {
            // given
            val instanceEntityMock = mockk<InstanceEntity>()
            every { instanceEntityMock.currentNode } returns
                NodeEntityGenerators.userActionNodeEntityGenerator.single()
            every { instanceRepositoryMock.findById(any()) } returns Optional.of(instanceEntityMock)

            val edgeId = 1L
            val cutSpy = spyk(cut)
            every { cutSpy.advanceInstanceNodeBySystem(instanceEntityMock, edgeId, null) } returns mockk()

            // when
            cutSpy.advanceInstanceNodeByUser(1L, edgeId)

            // then
            verify { cutSpy.advanceInstanceNodeBySystem(instanceEntityMock, edgeId, null) }
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent instance id") {
            // given
            val instanceId = 1L
            every { instanceRepositoryMock.findById(any()) } returns Optional.empty()

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.advanceInstanceNodeByUser(instanceId, 1L)
            }

            // then
            exception.message shouldBe "Workflow instance with id $instanceId could not be found"
        }

        should("throw NodeNotAdvanceableByUserRuleException when given current node without type user action") {
            // given
            val instanceEntityMock = mockk<InstanceEntity>()
            every { instanceEntityMock.currentNode } returns
                NodeEntityGenerators.automatedActionNodeEntityGenerator.single()
            every { instanceRepositoryMock.findById(any()) } returns Optional.of(instanceEntityMock)

            // when
            val exception = shouldThrow<NodeNotAdvanceableByUserRuleException> {
                cut.advanceInstanceNodeByUser(1L, 1L)
            }

            // then
            exception.message shouldBe "Node of type ${AutomatedActionNode::class.simpleName} " +
                "cannot be advanced by a user"
        }
    }

    context("advanceInstanceNodeBySystem (given instance id)") {
        should("call advanceInstanceNodeBySystem when given existing instance id") {
            // given
            val instanceEntity = InstanceEntityGenerators.generator.single()
            every { instanceRepositoryMock.findById(instanceEntity.id) } returns Optional.of(instanceEntity)

            val edgeId = 1L

            val cutSpy = spyk(cut)
            every { cutSpy.advanceInstanceNodeBySystem(instanceEntity, edgeId) } returns mockk()
            // when
            cutSpy.advanceInstanceNodeBySystem(instanceEntity.id, edgeId)

            // then
            verify { cutSpy.advanceInstanceNodeBySystem(instanceEntity, edgeId) }
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent instance id") {
            // given
            val instanceId = 1L
            every { instanceRepositoryMock.findById(any()) } returns Optional.empty()

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.advanceInstanceNodeBySystem(instanceId, 1L)
            }

            // then
            exception.message shouldBe "Workflow instance with id $instanceId could not be found"
        }
    }
})
