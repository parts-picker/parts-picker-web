package de.parts_picker.web.item.business.exceptions

class ItemNotFoundException(itemId: Long): Exception("Item with id $itemId could not be found")