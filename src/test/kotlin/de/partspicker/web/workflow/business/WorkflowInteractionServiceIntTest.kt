package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.WorkflowNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowStartedWithNonStartNodeException
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.maps.shouldContain
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
    private val instanceValueReadService: InstanceValueReadService
) : ShouldSpec({

    context("start workflow instance") {
        should("create an instance for the latest version of the given workflow starting at the given node") {
            // given
            val workflowName = "Testflows"
            val nodeName = "start"
            val values = mapOf("amount" to 5L)

            // when
            val instance = cut.startWorkflowInstance(workflowName, nodeName, values)

            // then
            instance.id shouldNotBe null
            instance.currentNode!!.name shouldBe nodeName
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
})
