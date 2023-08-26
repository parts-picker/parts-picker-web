package de.partspicker.web.inventory.business.objects

import de.partspicker.web.inventory.persistence.entities.RequiredItemTypeEntity
import de.partspicker.web.item.business.objects.ItemType

data class RequiredItemType(
    val projectId: Long,
    val itemType: ItemType,
    val assignedAmount: Long,
    val requiredAmount: Long
) {
    fun isRequiredAmountAssigned() = assignedAmount == requiredAmount

    companion object {
        fun from(requiredItemTypeEntity: RequiredItemTypeEntity, assignedAmount: Long) = RequiredItemType(
            projectId = requiredItemTypeEntity.project.id,
            itemType = ItemType.from(requiredItemTypeEntity.itemType),
            assignedAmount = assignedAmount,
            requiredAmount = requiredItemTypeEntity.requiredAmount
        )
    }

    init {
        require(assignedAmount >= 0) {
            "The assignedAmount of a requiredItemType must be greater than or equal to zero"
        }
        require(requiredAmount > 0) { "The requiredAmount of a requiredItemType must be greater than zero" }
    }
}
