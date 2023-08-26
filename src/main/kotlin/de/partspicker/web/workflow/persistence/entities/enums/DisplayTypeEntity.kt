package de.partspicker.web.workflow.persistence.entities.enums

import de.partspicker.web.workflow.business.objects.enums.DisplayType

enum class DisplayTypeEntity {
    DEFAULT,
    INFO,
    WARN,
    ERROR;

    companion object {
        fun from(displayType: DisplayType) = when (displayType) {
            DisplayType.DEFAULT -> DEFAULT
            DisplayType.INFO -> INFO
            DisplayType.WARN -> WARN
            DisplayType.ERROR -> ERROR
        }
    }
}
