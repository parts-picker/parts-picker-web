package de.partspicker.web.workflow.business

import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeSourceNotMatchingException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotActiveException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowStartedWithNonStartNodeException
import de.partspicker.web.workflow.business.objects.EdgeInfo
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistance.InstanceRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.maps.shouldHaveSize
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
    private val instanceRepository: InstanceRepository
) : ShouldSpec({

    context("read instance info") {
        should("return instance info when given existing instance id") {
            // given
            val instanceId = 1L

            // when
            val instanceInfo = cut.readInstanceInfo(instanceId)

            // then
            instanceInfo shouldNotBe null
            instanceInfo!!.name shouldBe "start"
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
            val projectId = 1L

            // when
            val projectStatus = cut.readProjectStatus(projectId)

            // then
            projectStatus shouldBe "start"
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

    context("start workflow instance") {
        should("create an instance for the latest workflow version starting at the successor of the given start") {
            // given
            val workflowName = "Testflows"
            val nodeName = "start"
            val values = mapOf("amount" to 5L)

            // when
            val instance = cut.startWorkflowInstance(workflowName, nodeName, values)

            // then
            instance.id shouldNotBe null
            instance.currentNode!!.name shouldNotBe nodeName
            instance.currentNode!!.name shouldBe "planning"
            instance.active shouldBe true

            val readValues = instanceValueReadService.readAllForInstance(instance.id)

            readValues shouldHaveSize 1
            readValues shouldContain ("amount" to ("5" to SupportedDataType.LONG))
        }

        should("throw WorkflowNameNotFoundException when given non-existent workflow name") {
            // given
            val nonExistentWorkflowName = "nonExistentName"

            // when
            val exception = shouldThrow<WorkflowNameNotFoundException> {
                cut.startWorkflowInstance(nonExistentWorkflowName, "someNode")
            }

            // then
            exception.message shouldBe "Workflow with name $nonExistentWorkflowName could not be found"
        }

        should("throw WorkflowNodeNameNotFoundException when given non-existent workflow name") {
            // given
            val workflowName = "Testflows"
            val nonExistentNode = "nonExistentNode"

            // when
            val exception = shouldThrow<WorkflowNodeNameNotFoundException> {
                cut.startWorkflowInstance(workflowName, nonExistentNode)
            }

            // then
            exception.message shouldBe "Workflow node with name $nonExistentNode " +
                "could not be found for workflow with name $workflowName"
        }

        should("throw WorkflowStartedWithNonStartNodeException when given non-existent workflow name") {
            // given
            val workflowName = "Testflows"
            val nodeName = "planning"

            // when
            val exception = shouldThrow<WorkflowStartedWithNonStartNodeException> {
                cut.startWorkflowInstance(workflowName, nodeName)
            }

            // then
            exception.message shouldBe "Workflow can only be started at a start node." +
                " The node with name $nodeName is not a start node of the workflow with name $workflowName"
        }
    }

    context("advance workflow instance state") {
        should("advance to the next node with the given values") {
            // given
            val instanceId = 1L
            val edgeId = 100L
            val values = mapOf("amount" to 3, "owner" to "someone")

            // when
            val instanceInfo = cut.advanceInstanceNodeThroughEdge(instanceId, edgeId, values)

            // then
            instanceInfo!!
            instanceInfo.name shouldBe "planning"
            instanceInfo.displayName shouldBe "Planning"

            val instanceValues = instanceValueReadService.readAllForInstance(instanceId)

            instanceValues shouldHaveSize values.size
            instanceValues.mapValues { it.value.first } shouldContainAll values.mapValues { it.value.toString() }
        }

        should("deactivate the instance when advancing to stop node") {
            // given
            val instanceId = 3L
            val edgeId = 400L

            // when
            val instanceInfo = cut.advanceInstanceNodeThroughEdge(instanceId, edgeId)

            // then
            instanceInfo!!
            instanceInfo.name shouldBe "stop"
            instanceInfo.displayName shouldBe "Stop"

            val instance = instanceRepository.findById(instanceId).get()
            instance.active shouldBe true
            instance.currentNode!!.name shouldBe "stop"
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent instance id ") {
            // given
            val nonExistentId = 666L
            val edgeId = 100L

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.advanceInstanceNodeThroughEdge(nonExistentId, edgeId)
            }

            // then
            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }

        should("throw WorkflowInstanceNotActiveException when given inactive instance") {
            // given
            val instanceId = 2L
            val edgeId = 100L

            // when
            val exception = shouldThrow<WorkflowInstanceNotActiveException> {
                cut.advanceInstanceNodeThroughEdge(instanceId, edgeId)
            }

            // then
            exception.message shouldBe "Workflow instance with id $instanceId cannot be edited because it is inactive."
        }

        should("throw WorkflowEdgeNotFoundException when given non-existent instance id ") {
            // given
            val instanceId = 1L
            val nonExistentId = 666L

            // when
            val exception = shouldThrow<WorkflowEdgeNotFoundException> {
                cut.advanceInstanceNodeThroughEdge(instanceId, nonExistentId)
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
                cut.advanceInstanceNodeThroughEdge(instanceId, edgeId)
            }

            // then
            exception.message shouldBe "The current instance node with id 100 does not match the source node" +
                " with id 300 of the given edge with id $edgeId to advance the instance state"
        }
    }

    context("force instance node") {
        should("set instance to node with given name & return the updated instance info") {
            // given
            val instanceId = 1L
            val nodeName = "implementation"

            // when
            val updatedInstance = cut.forceInstanceNode(instanceId, nodeName)!!

            // then
            updatedInstance.instanceId shouldBe instanceId
            updatedInstance.name shouldBe nodeName
            updatedInstance.displayName shouldBe "Implementation"
        }

        should("throw WorkflowInstanceNotFoundException when given non-existing instance id") {
            // given
            val nonExistentId = 666L
            val nodeName = "implementation"

            // when & then
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.forceInstanceNode(
                    nonExistentId,
                    nodeName
                )!!
            }

            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }

        should("throw WorkflowNodeNameNotFoundException when given non-existing node name") {
            // given
            val instanceId = 1L
            val nonExistentNodeName = "non-existent node name"

            // when & then
            val exception = shouldThrow<WorkflowNodeNameNotFoundException> {
                cut.forceInstanceNode(
                    instanceId,
                    nonExistentNodeName
                )!!
            }

            exception.message shouldBe
                "Workflow node with name $nonExistentNodeName could not be found for workflow with name Testflows"
        }
    }
})
