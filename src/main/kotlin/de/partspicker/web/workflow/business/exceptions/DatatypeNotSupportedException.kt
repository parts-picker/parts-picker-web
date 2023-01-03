package de.partspicker.web.workflow.business.exceptions

import de.partspicker.web.workflow.business.objects.enums.SupportedDataType

class DatatypeNotSupportedException(dataType: String) : RuntimeException(
    "The given data with datatype '$dataType' is not supported. " +
        "Supported types are ${SupportedDataType.values().map { it.name }}"
)
