package de.partspicker.web.workflow.persistence.entities.enums

import de.partspicker.web.workflow.business.objects.enums.SupportedDataType

enum class SupportedDataTypeEntity {
    STRING,
    LONG,
    INTEGER;

    companion object {
        fun from(supportedDataType: SupportedDataType) = when (supportedDataType) {
            SupportedDataType.STRING -> STRING
            SupportedDataType.LONG -> LONG
            SupportedDataType.INTEGER -> INTEGER
        }
    }
}
