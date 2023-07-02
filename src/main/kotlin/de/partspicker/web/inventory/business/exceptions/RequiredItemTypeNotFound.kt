package de.partspicker.web.inventory.business.exceptions

class RequiredItemTypeNotFound(projectId: Long, itemTypeId: Long) :
    Exception("No items of item type with id $itemTypeId required for project with id $projectId")
