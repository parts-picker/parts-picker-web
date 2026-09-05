package de.partspicker.web.inventory.business.exceptions

class InvalidEnumConversionException(sourceEnumValue: String) : RuntimeException(
    "Tried converting on enum to another, but value '$sourceEnumValue' is not available in target enum"
)
