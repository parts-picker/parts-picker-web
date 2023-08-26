package de.partspicker.web.workflow.business.objects.enums

import de.partspicker.web.workflow.persistence.entities.enums.SupportedDataTypeEntity
import de.partspicker.web.workflow.persistence.entities.migration.enums.SupportedDataTypeMigrationEntity

enum class SupportedDataType {
    STRING,
    LONG,
    INTEGER;

    companion object {
        fun from(supportedDataTypeEntity: SupportedDataTypeEntity) = when (supportedDataTypeEntity) {
            SupportedDataTypeEntity.STRING -> STRING
            SupportedDataTypeEntity.LONG -> LONG
            SupportedDataTypeEntity.INTEGER -> INTEGER
        }

        fun from(supportedDataTypeMigrationEntity: SupportedDataTypeMigrationEntity) =
            when (supportedDataTypeMigrationEntity) {
                SupportedDataTypeMigrationEntity.STRING -> STRING
                SupportedDataTypeMigrationEntity.LONG -> LONG
                SupportedDataTypeMigrationEntity.INTEGER -> INTEGER
            }
    }
}
