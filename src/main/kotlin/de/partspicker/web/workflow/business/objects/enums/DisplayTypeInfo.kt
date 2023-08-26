package de.partspicker.web.workflow.business.objects.enums

import de.partspicker.web.workflow.persistence.entities.enums.DisplayTypeEntity

enum class DisplayTypeInfo {
    DEFAULT,
    INFO,
    WARN,
    ERROR;

    companion object {
        fun from(displayTypeEntity: DisplayTypeEntity) = when (displayTypeEntity) {
            DisplayTypeEntity.DEFAULT -> DEFAULT
            DisplayTypeEntity.INFO -> INFO
            DisplayTypeEntity.WARN -> WARN
            DisplayTypeEntity.ERROR -> ERROR
        }
    }
}
