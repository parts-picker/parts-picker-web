package de.partspicker.web.item.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class ItemNotFoundException(itemId: Long) : EntityNotFoundException("Item with id $itemId could not be found")
