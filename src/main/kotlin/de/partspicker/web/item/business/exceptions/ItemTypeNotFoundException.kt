package de.partspicker.web.item.business.exceptions

class ItemTypeNotFoundException(itemTypeId: Long) : Exception("ItemType with id $itemTypeId could not be found")
