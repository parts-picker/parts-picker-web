package de.partspicker.web.workflow.business.objects.enums

import de.partspicker.web.workflow.persistance.entities.enums.SupportedDataTypeEntity

enum class SupportedDataType {
    STRING,
    LONG;

    companion object {
        fun from(supportedDataTypeEntity: SupportedDataTypeEntity) = when (supportedDataTypeEntity) {
            SupportedDataTypeEntity.STRING -> STRING
            SupportedDataTypeEntity.LONG -> LONG
        }
    }
}
