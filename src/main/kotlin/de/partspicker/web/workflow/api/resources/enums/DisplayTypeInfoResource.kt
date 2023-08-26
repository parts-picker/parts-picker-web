package de.partspicker.web.workflow.api.resources.enums

import de.partspicker.web.workflow.business.objects.enums.DisplayTypeInfo

enum class DisplayTypeInfoResource {
    DEFAULT,
    INFO,
    WARN,
    ERROR;

    companion object {
        fun from(displayTypeInfo: DisplayTypeInfo) = when (displayTypeInfo) {
            DisplayTypeInfo.DEFAULT -> DEFAULT
            DisplayTypeInfo.INFO -> INFO
            DisplayTypeInfo.WARN -> WARN
            DisplayTypeInfo.ERROR -> ERROR
        }
    }
}
