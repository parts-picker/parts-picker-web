package de.partspicker.web.inventory.business.exceptions

class InvalidEnumConversionException(sourceEnumValue: String) : Exception(
    "Tried converting on enum to another, but value '$sourceEnumValue' is not available in target enum"
)
