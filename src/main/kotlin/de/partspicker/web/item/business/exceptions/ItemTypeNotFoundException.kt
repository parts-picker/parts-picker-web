package de.partspicker.web.item.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class ItemTypeNotFoundException(itemTypeId: Long) :
    EntityNotFoundException("ItemType with id $itemTypeId could not be found")
