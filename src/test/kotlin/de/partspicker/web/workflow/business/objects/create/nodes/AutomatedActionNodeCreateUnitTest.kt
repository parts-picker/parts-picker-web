package de.partspicker.web.workflow.business.objects.create.nodes

import de.partspicker.web.test.generators.workflow.NodeCreateGenerators
import de.partspicker.web.workflow.business.exceptions.WorkflowIllegalStateException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class AutomatedActionNodeCreateUnitTest : ShouldSpec({

    context("validation") {
        should("run when everything valid") {
            checkAll(NodeCreateGenerators.automatedActionNodeCreateGenerator) {
                // just run
            }
        }

        should("throw WorkflowIllegalStateException when given blank name") {
            // given & when
            val exception = shouldThrow<WorkflowIllegalStateException> {
                AutomatedActionNodeCreate(
                    name = "",
                    displayName = "not blank",
                    automatedActionName = "not blank"
                )
            }

            // then
            exception.message shouldBe NodeCreate.NAME_IS_BLANK
        }

        should("throw WorkflowIllegalStateException when given blank displayName") {
            // given & when
            val exception = shouldThrow<WorkflowIllegalStateException> {
                AutomatedActionNodeCreate(
                    name = "not blank",
                    displayName = "",
                    automatedActionName = "not blank"
                )
            }

            // then
            exception.message shouldBe AutomatedActionNodeCreate.DISPLAY_NAME_IS_BLANK
        }

        should("throw WorkflowIllegalStateException when given blank automatedActionName") {
            // given & when
            val exception = shouldThrow<WorkflowIllegalStateException> {
                AutomatedActionNodeCreate(
                    name = "not blank",
                    displayName = "not blank",
                    automatedActionName = ""
                )
            }

            // then
            exception.message shouldBe AutomatedActionNodeCreate.AUTOMATED_ACTION_NAME_IS_BLANK
        }
    }
})
