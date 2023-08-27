package de.partspicker.web.workflow.business

import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.test.util.TestSetupHelper
import de.partspicker.web.workflow.business.exceptions.InstanceInactiveException
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeSourceNotMatchingException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.EdgeInfo
import de.partspicker.web.workflow.business.objects.InstanceValue
import de.partspicker.web.workflow.business.objects.enums.InstanceValueType
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistence.InstanceRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/workflowInteractionServiceIntTest.sql")
class WorkflowInteractionServiceIntTest(
    private val cut: WorkflowInteractionService,
    // support classes
    private val instanceValueReadService: InstanceValueReadService,
    private val instanceRepository: InstanceRepository,
    private val testSetupHelper: TestSetupHelper
) : ShouldSpec({

    context("read instance info") {
        should("return instance info when given existing instance id") {
            // given
            val instanceId = 1L

            // when
            val instanceInfo = cut.readInstanceInfo(instanceId)

            // then
            instanceInfo shouldNotBe null
            instanceInfo.name shouldBe "start"
            instanceInfo.displayName shouldBe "Start"
            instanceInfo.nodeId shouldBe 100L
            instanceInfo.instanceId shouldBe instanceId

            instanceInfo.options shouldHaveSize 1
            instanceInfo.options shouldContain EdgeInfo(
                100L,
                "start_to_planning",
                "Start",
                100L,
                instanceId
            )
        }

        should("throw WorkflowInstanceNotFoundException when given non-existing instance id") {
            // given
            val nonExistentId = 666L

            // when & then
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.readInstanceInfo(nonExistentId)
            }

            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }
    }

    context("read project status") {
        should("return project status when given existing id") {
            // given
            val project = testSetupHelper.setupProject()

            // when
            val projectStatus = cut.readProjectStatus(project.id)

            // then
            projectStatus shouldBe project.status
        }

        should("throw ProjectNotFoundException when given non-existing id") {
            // given
            val nonExistentId = 666L

            // when & then
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.readProjectStatus(nonExistentId)
            }

            exception.message shouldBe "Project with id $nonExistentId could not be found"
        }
    }

    context("read edges by source node id") {
        should("return all edges with source node being the node with the given id ") {
            // given
            val sourceNodeId = 1000L

            // when
            val edges = cut.readEdgesBySourceNodeId(sourceNodeId)

            // then
            edges shouldHaveSize 2
            edges.map { it.id } shouldContainOnly listOf(1000L, 1002L)
        }
    }

    context("start workflow instance") {
        should("create an instance for the latest workflow version starting at the successor of the given start") {
            // given
            val workflowName = "Testflows"
            val nodeName = "start"
            val values = listOf(
                InstanceValue(
                    key = "amount",
                    value = "5",
                    dataType = SupportedDataType.LONG,
                    valueType = InstanceValueType.WORKFLOW
                )
            )

            // when
            val instance = cut.startWorkflowInstance(workflowName, nodeName, values)

            // then
            instance.id shouldNotBe null
            instance.currentNode!!.name shouldNotBe nodeName
            instance.currentNode!!.name shouldBe "planning"
            instance.active shouldBe true

            val readValues = instanceValueReadService.readAllForInstance(instance.id)

            readValues shouldHaveSize 1
            readValues shouldContainOnly listOf(
                InstanceValue(
                    key = "amount",
                    value = "5",
                    dataType = SupportedDataType.LONG,
                    valueType = InstanceValueType.WORKFLOW
                )
            )
        }
    }

    context("advanceInstanceNodeBySystem") {
        should("advance to the next node with the given values") {
            // given
            val instanceId = 1L
            val edgeId = 100L
            val values = listOf(
                InstanceValue("amount", "3", SupportedDataType.INTEGER, InstanceValueType.WORKFLOW),
                InstanceValue("owner", "someone", SupportedDataType.STRING, InstanceValueType.WORKFLOW)
            )

            // when
            val instanceInfo = cut.advanceInstanceNodeBySystem(instanceId, edgeId, values)

            // then
            instanceInfo.name shouldBe "planning"
            instanceInfo.displayName shouldBe "Planning"

            val instanceValues = instanceValueReadService.readAllForInstance(instanceId)

            instanceValues shouldHaveSize values.size
            instanceValues.map { it.key } shouldContainAll values.map { it.key }
        }

        should("deactivate the instance when advancing to stop node") {
            // given
            val instanceId = 3L
            val edgeId = 400L

            // when
            val instanceInfo = cut.advanceInstanceNodeBySystem(instanceId, edgeId)

            // then
            instanceInfo.name shouldBe "stop"
            instanceInfo.displayName shouldBe "Stop"

            val instance = instanceRepository.findById(instanceId).get()
            instance.active shouldBe true
            instance.currentNode.name shouldBe "stop"
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent instance id ") {
            // given
            val nonExistentId = 666L
            val edgeId = 100L

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.advanceInstanceNodeBySystem(nonExistentId, edgeId)
            }

            // then
            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }

        should("throw WorkflowInstanceNotActiveException when given inactive instance") {
            // given
            val instanceId = 2L
            val edgeId = 100L

            // when
            val exception = shouldThrow<InstanceInactiveException> {
                cut.advanceInstanceNodeBySystem(instanceId, edgeId)
            }

            // then
            exception.message shouldBe "The instance with the given id $instanceId is inactive & cannot be modified"
        }

        should("throw WorkflowEdgeNotFoundException when given non-existent instance id ") {
            // given
            val instanceId = 1L
            val nonExistentId = 666L

            // when
            val exception = shouldThrow<WorkflowEdgeNotFoundException> {
                cut.advanceInstanceNodeBySystem(instanceId, nonExistentId)
            }

            // then
            exception.message shouldBe "Workflow edge with id $nonExistentId could not be found"
        }

        should("throw WorkflowEdgeSourceNotMatchingException when current node does match edge source node") {
            // given
            val instanceId = 1L
            val edgeId = 300L

            // when
            val exception = shouldThrow<WorkflowEdgeSourceNotMatchingException> {
                cut.advanceInstanceNodeBySystem(instanceId, edgeId)
            }

            // then
            exception.message shouldBe "The current instance node with id 100 does not match the source node" +
                " with id 300 of the given edge with id $edgeId to advance the instance state"
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
