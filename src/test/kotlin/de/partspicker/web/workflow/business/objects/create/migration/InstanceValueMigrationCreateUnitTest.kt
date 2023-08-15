package de.partspicker.web.workflow.business.objects.create.migration

import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationIllegalArgumentException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationValueHasWrongTypeException
import de.partspicker.web.workflow.business.objects.create.migration.enums.SupportedDataTypeMigrationCreate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class InstanceValueMigrationCreateUnitTest : ShouldSpec({

    context("validation") {
        should("just run when valid") {
            InstanceValueMigrationCreate(
                key = "KEY",
                value = "VALUE",
                type = SupportedDataTypeMigrationCreate.STRING
            )
        }

        should("throw WorkflowMigrationIllegalArgumentException when given an empty string as key") {
            // given
            val key = ""

            // when
            val exception = shouldThrow<WorkflowMigrationIllegalArgumentException> {
                InstanceValueMigrationCreate(
                    key = key,
                    value = "VALUE",
                    type = SupportedDataTypeMigrationCreate.STRING
                )
            }

            // then
            exception.message shouldBe InstanceValueMigrationCreate.KEY_EMPTY
        }

        should("throw Exception when given type LONG & a value that cannot be parsed to LONG") {
            // given
            val notALongValue = "A VALUE THAT IS NOT A LONG"
            val type = SupportedDataTypeMigrationCreate.LONG

            // when
            val exception = shouldThrow<WorkflowMigrationValueHasWrongTypeException> {
                InstanceValueMigrationCreate(
                    key = "KEY",
                    value = notALongValue,
                    type = type
                )
            }

            // then
            exception.message shouldBe
                "The given value '$notALongValue' cannot be parsed to the expected type '$type'"
        }

        should("throw Exception when given type INT & a value that cannot be parsed to INT") {
            // given
            val notAnIntValue = "A VALUE THAT IS NOT AN INT"
            val type = SupportedDataTypeMigrationCreate.INTEGER

            // when
            val exception = shouldThrow<WorkflowMigrationValueHasWrongTypeException> {
                InstanceValueMigrationCreate(
                    key = "KEY",
                    value = notAnIntValue,
                    type = type
                )
            }

            // then
            exception.message shouldBe
                "The given value '$notAnIntValue' cannot be parsed to the expected type '$type'"
        }
    }
})
