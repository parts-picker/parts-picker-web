package de.partspicker.web.inventory.business.exceptions

import de.partspicker.web.common.exceptions.EntityNotFoundException

class RequiredItemTypeNotFound(projectId: Long, itemTypeId: Long) :
    EntityNotFoundException("No items of item type with id $itemTypeId required for project with id $projectId")
