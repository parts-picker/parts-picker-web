package de.partspicker.web.workflow.persistence.entities.enums

import de.partspicker.web.workflow.business.objects.create.enums.SupportedDataTypeCreate

enum class SupportedDataTypeEntity {
    STRING,
    LONG,
    INTEGER;

    companion object {
        fun from(supportedDataTypeCreate: SupportedDataTypeCreate) = when (supportedDataTypeCreate) {
            SupportedDataTypeCreate.STRING -> STRING
            SupportedDataTypeCreate.LONG -> LONG
            SupportedDataTypeCreate.INTEGER -> INTEGER
        }
    }
}
