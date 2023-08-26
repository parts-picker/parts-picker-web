package de.partspicker.web.workflow.api.json.migration

import de.partspicker.web.workflow.api.json.migration.enums.InstanceValueTypeJson
import de.partspicker.web.workflow.api.json.migration.enums.SupportedDataTypeJson

data class InstanceValueMigrationJson(
    val key: String,
    val value: String?,
    val dataType: SupportedDataTypeJson,
    val valueType: InstanceValueTypeJson
)
