package de.partspicker.web.workflow.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.orgunit.business.exceptions.OrgUnitAccessDeniedException
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.ProjectEntityGenerators
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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import io.mockk.clearMocks
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
    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()

    val cut = WorkflowInteractionService(
        workflowRepository = workflowRepositoryMock,
        instanceRepository = instanceRepositoryMock,
        nodeRepository = nodeRepositoryMock,
        edgeRepository = edgeRepositoryMock,
        instanceValueService = instanceValueServiceMock,
        projectRepository = projectRepositoryMock,
        orgUnitAccessService = orgUnitAccessServiceMock
    )

    afterTest {
        clearMocks(
            workflowRepositoryMock,
            instanceRepositoryMock,
            nodeRepositoryMock,
            edgeRepositoryMock,
            instanceValueServiceMock,
            projectRepositoryMock,
            orgUnitAccessServiceMock
        )
    }

    context("readProjectStatus") {
        should("refuse when the caller may not read the org unit of the given project") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.single()
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)
            every {
                orgUnitAccessServiceMock.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.READ)
            } throws OrgUnitAccessDeniedException(projectEntity.orgUnit.id, AccessLevel.READ)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.readProjectStatus(projectEntity.id) }
        }
    }

    context("readProjectInstanceInfo") {
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

            val projectEntity = ProjectEntityGenerators.generator.single().copy(workflowInstance = instanceEntity)
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)
            every {
                orgUnitAccessServiceMock.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.READ)
            } returns Unit
            every { instanceRepositoryMock.findById(instanceEntity.id) } returns Optional.of(instanceEntity)
            every { edgeRepositoryMock.findAllBySourceId(currentNodeEntity.id) } returns options

            // when
            val returnedNodeInfo = cut.readProjectInstanceInfo(projectEntity.id)

            // then
            returnedNodeInfo.nodeId shouldBe instanceEntity.currentNode.id
            returnedNodeInfo.name shouldBe instanceEntity.currentNode.name
            returnedNodeInfo.instanceId shouldBe instanceEntity.id
            returnedNodeInfo.options shouldHaveSize options.size
        }

        should("throw WorkflowInstanceNotFoundException when the project references a non-existent instance") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.single()
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)
            every {
                orgUnitAccessServiceMock.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.READ)
            } returns Unit
            every { instanceRepositoryMock.findById(any()) } returns Optional.empty()

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.readProjectInstanceInfo(projectEntity.id)
            }

            // then
            exception.message shouldBe
                "Workflow instance with id ${projectEntity.workflowInstance.id} could not be found"
        }

        should("refuse & not read when the caller may not read the org unit of the given project") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.single()
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)
            every {
                orgUnitAccessServiceMock.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.READ)
            } throws OrgUnitAccessDeniedException(projectEntity.orgUnit.id, AccessLevel.READ)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.readProjectInstanceInfo(projectEntity.id) }

            verify(exactly = 0) { instanceRepositoryMock.findById(any()) }
        }

        should("throw ProjectNotFoundException when given non-existent project id") {
            // given
            val nonExistentId = 666L
            every { projectRepositoryMock.findById(nonExistentId) } returns Optional.empty()

            // when & then
            shouldThrow<ProjectNotFoundException> { cut.readProjectInstanceInfo(nonExistentId) }
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

    context("advanceInstanceNodeBySystem") {
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

    context("advanceProjectStateByUser") {
        should("advance the instance of the given project when its current node is a user action") {
            // given
            val instanceEntity = InstanceEntityGenerators.generator.single()
                .copy(currentNode = NodeEntityGenerators.userActionNodeEntityGenerator.single())
            val projectEntity = ProjectEntityGenerators.generator.single().copy(workflowInstance = instanceEntity)
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)
            every { orgUnitAccessServiceMock.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.USE) } returns Unit
            every { instanceRepositoryMock.findById(instanceEntity.id) } returns Optional.of(instanceEntity)

            val edgeId = 1L
            val cutSpy = spyk(cut)
            every { cutSpy.advanceInstanceNodeBySystem(instanceEntity, edgeId, null) } returns mockk()

            // when
            cutSpy.advanceProjectStateByUser(projectEntity.id, edgeId)

            // then
            verify { cutSpy.advanceInstanceNodeBySystem(instanceEntity, edgeId, null) }
        }

        should("throw NodeNotAdvanceableByUserRuleException when the current node is no user action") {
            // given
            val instanceEntity = InstanceEntityGenerators.generator.single()
                .copy(currentNode = NodeEntityGenerators.automatedActionNodeEntityGenerator.single())
            val projectEntity = ProjectEntityGenerators.generator.single().copy(workflowInstance = instanceEntity)
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)
            every { orgUnitAccessServiceMock.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.USE) } returns Unit
            every { instanceRepositoryMock.findById(instanceEntity.id) } returns Optional.of(instanceEntity)

            // when
            val exception = shouldThrow<NodeNotAdvanceableByUserRuleException> {
                cut.advanceProjectStateByUser(projectEntity.id, 1L)
            }

            // then
            exception.message shouldBe "Node of type ${AutomatedActionNode::class.simpleName} " +
                "cannot be advanced by a user"
        }

        should("throw WorkflowInstanceNotFoundException when the project references a non-existent instance") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.single()
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)
            every { orgUnitAccessServiceMock.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.USE) } returns Unit
            every { instanceRepositoryMock.findById(any()) } returns Optional.empty()

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.advanceProjectStateByUser(projectEntity.id, 1L)
            }

            // then
            exception.message shouldBe
                "Workflow instance with id ${projectEntity.workflowInstance.id} could not be found"
        }

        should("refuse & not advance when the caller may not edit the org unit of the given project") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.single()
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)
            every {
                orgUnitAccessServiceMock.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.USE)
            } throws OrgUnitAccessDeniedException(projectEntity.orgUnit.id, AccessLevel.USE)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.advanceProjectStateByUser(projectEntity.id, 1L) }

            verify(exactly = 0) { instanceRepositoryMock.save(any()) }
        }

        should("throw ProjectNotFoundException when given non-existent project id") {
            // given
            val nonExistentId = 666L
            every { projectRepositoryMock.findById(nonExistentId) } returns Optional.empty()

            // when & then
            shouldThrow<ProjectNotFoundException> { cut.advanceProjectStateByUser(nonExistentId, 1L) }
        }
    }
})
