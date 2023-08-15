package de.partspicker.web.workflow.business.objects.create.migration

import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationDuplicatedInstanceValueKeyException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationIllegalArgumentException
import de.partspicker.web.workflow.business.objects.create.migration.enums.SupportedDataTypeMigrationCreate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class NodeMigrationCreateUnitTest : ShouldSpec({

    context("validation") {
        should("just run when valid") {
            NodeMigrationCreate(
                sourceNode = "from",
                targetNode = "to",
                instanceValueMigrations = listOf(
                    InstanceValueMigrationCreate(
                        key = "KEY",
                        value = "VALUE",
                        type = SupportedDataTypeMigrationCreate.STRING
                    ),
                    InstanceValueMigrationCreate(
                        key = "KEY2",
                        value = "VALUE",
                        type = SupportedDataTypeMigrationCreate.STRING
                    )
                )
            )
        }

        should("throw WorkflowMigrationIllegalArgumentException when given an empty string as source node") {
            // given & when
            val exception = shouldThrow<WorkflowMigrationIllegalArgumentException> {
                NodeMigrationCreate(
                    sourceNode = "",
                    targetNode = "to",
                    instanceValueMigrations = emptyList()
                )
            }

            // then
            exception.message shouldBe NodeMigrationCreate.SOURCE_NODE_IS_EMPTY
        }

        should("throw WorkflowMigrationIllegalArgumentException when given an empty string as target node") {
            // given & when
            val exception = shouldThrow<WorkflowMigrationIllegalArgumentException> {
                NodeMigrationCreate(
                    sourceNode = "from",
                    targetNode = "",
                    instanceValueMigrations = emptyList()
                )
            }

            // then
            exception.message shouldBe NodeMigrationCreate.TARGET_NODE_IS_EMPTY
        }

        should("throw Exception when instance value migrations with duplicated key") {
            // given
            val sourceNode = "from"
            val targetNode = "to"

            // when
            val exception = shouldThrow<WorkflowMigrationDuplicatedInstanceValueKeyException> {
                NodeMigrationCreate(
                    sourceNode = sourceNode,
                    targetNode = targetNode,
                    instanceValueMigrations = listOf(
                        InstanceValueMigrationCreate(
                            key = "KEY",
                            value = "VALUE",
                            type = SupportedDataTypeMigrationCreate.STRING
                        ),
                        InstanceValueMigrationCreate(
                            key = "KEY",
                            value = "VALUE",
                            type = SupportedDataTypeMigrationCreate.STRING
                        )
                    )
                )
            }

            // then
            exception.message shouldBe
                "Keys must be unique within a node migration rule but there are duplicates: ${setOf("KEY")} " +
                "for rule '$sourceNode' --> '$targetNode'"
        }
    }
})
