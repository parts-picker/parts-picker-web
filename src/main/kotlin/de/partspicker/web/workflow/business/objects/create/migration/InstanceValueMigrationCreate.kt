package de.partspicker.web.workflow.business.objects.create.migration

import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.workflow.api.json.migration.InstanceValueMigrationJson
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationIllegalArgumentException
import de.partspicker.web.workflow.business.objects.create.migration.enums.InstanceValueTypeCreate
import de.partspicker.web.workflow.business.objects.create.migration.enums.SupportedDataTypeMigrationCreate

data class InstanceValueMigrationCreate(
    val key: String,
    val value: String?,
    val dataType: SupportedDataTypeMigrationCreate,
    val valueType: InstanceValueTypeCreate
) {
    companion object {
        const val KEY_EMPTY = "Key must be a non-empty string"

        fun from(instanceValueMigrationJson: InstanceValueMigrationJson) = InstanceValueMigrationCreate(
            key = instanceValueMigrationJson.key,
            value = instanceValueMigrationJson.value,
            dataType = SupportedDataTypeMigrationCreate.from(instanceValueMigrationJson.dataType),
            valueType = InstanceValueTypeCreate.from(instanceValueMigrationJson.valueType)
        )
    }

    object AsList {
        fun from(instanceValueMigrationJsons: Iterable<InstanceValueMigrationJson>) =
            instanceValueMigrationJsons.map { from(it) }
    }

    init {
        key.isNotBlank() elseThrow WorkflowMigrationIllegalArgumentException(KEY_EMPTY)
    }
}
