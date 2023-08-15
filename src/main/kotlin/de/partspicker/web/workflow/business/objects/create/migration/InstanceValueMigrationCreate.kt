package de.partspicker.web.workflow.business.objects.create.migration

import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.workflow.api.json.migration.InstanceValueMigrationJson
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationIllegalArgumentException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationValueHasWrongTypeException
import de.partspicker.web.workflow.business.objects.create.migration.enums.SupportedDataTypeMigrationCreate

data class InstanceValueMigrationCreate(
    val key: String,
    val value: String?,
    val type: SupportedDataTypeMigrationCreate
) {
    companion object {
        const val KEY_EMPTY = "Key must be a non-empty string"
        fun from(instanceValueMigrationJson: InstanceValueMigrationJson) = InstanceValueMigrationCreate(
            key = instanceValueMigrationJson.key,
            value = instanceValueMigrationJson.value,
            type = SupportedDataTypeMigrationCreate.from(instanceValueMigrationJson.type)
        )
    }

    object AsList {
        fun from(instanceValueMigrationJsons: Iterable<InstanceValueMigrationJson>) =
            instanceValueMigrationJsons.map { from(it) }
    }

    init {
        key.isNotBlank() elseThrow WorkflowMigrationIllegalArgumentException(KEY_EMPTY)

        if (value != null) {
            when (type) {
                SupportedDataTypeMigrationCreate.LONG ->
                    value.toLongOrNull() ?: throw WorkflowMigrationValueHasWrongTypeException(value, type)

                SupportedDataTypeMigrationCreate.INTEGER ->
                    value.toIntOrNull() ?: throw WorkflowMigrationValueHasWrongTypeException(value, type)

                SupportedDataTypeMigrationCreate.STRING ->
                    Unit
            }
        }
    }
}
