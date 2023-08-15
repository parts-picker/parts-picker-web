package de.partspicker.web.workflow.business.objects.create.migration.enums

import de.partspicker.web.workflow.api.json.migration.enums.SupportedDataTypeJson

enum class SupportedDataTypeMigrationCreate {
    STRING,
    LONG,
    INTEGER;

    companion object {
        fun from(supportedDataTypeJson: SupportedDataTypeJson) = when (supportedDataTypeJson) {
            SupportedDataTypeJson.STRING -> STRING
            SupportedDataTypeJson.LONG -> LONG
            SupportedDataTypeJson.INTEGER -> INTEGER
        }
    }
}
