package de.partspicker.web.workflow.business.objects.create.enums

import de.partspicker.web.workflow.persistence.entities.migration.enums.SupportedDataTypeMigrationEntity

enum class SupportedDataTypeCreate {
    STRING,
    LONG,
    INTEGER;

    companion object {
        fun from(supportedDataTypeMigrationEntity: SupportedDataTypeMigrationEntity) =
            when (supportedDataTypeMigrationEntity) {
                SupportedDataTypeMigrationEntity.STRING -> STRING
                SupportedDataTypeMigrationEntity.LONG -> LONG
                SupportedDataTypeMigrationEntity.INTEGER -> INTEGER
            }
    }
}
