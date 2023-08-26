package de.partspicker.web.workflow.business.objects.create.migration

import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationIllegalArgumentException
import de.partspicker.web.workflow.business.objects.create.migration.enums.InstanceValueTypeCreate
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
                dataType = SupportedDataTypeMigrationCreate.STRING,
                valueType = InstanceValueTypeCreate.WORKFLOW
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
                    dataType = SupportedDataTypeMigrationCreate.STRING,
                    valueType = InstanceValueTypeCreate.WORKFLOW
                )
            }

            // then
            exception.message shouldBe InstanceValueMigrationCreate.KEY_EMPTY
        }
    }
})
