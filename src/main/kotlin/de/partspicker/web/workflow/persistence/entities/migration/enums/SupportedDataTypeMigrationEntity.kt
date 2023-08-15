package de.partspicker.web.workflow.persistence.entities.migration.enums

import de.partspicker.web.workflow.business.objects.create.migration.enums.SupportedDataTypeMigrationCreate

enum class SupportedDataTypeMigrationEntity {
    STRING,
    LONG,
    INTEGER;

    companion object {
        fun from(supportedDataTypeMigrationCreate: SupportedDataTypeMigrationCreate) =
            when (supportedDataTypeMigrationCreate) {
                SupportedDataTypeMigrationCreate.STRING -> STRING
                SupportedDataTypeMigrationCreate.LONG -> LONG
                SupportedDataTypeMigrationCreate.INTEGER -> INTEGER
            }
    }
}
