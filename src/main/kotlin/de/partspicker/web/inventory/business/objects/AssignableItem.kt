package de.partspicker.web.inventory.business.objects

import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.inventory.business.exceptions.AssignableItemMustBeInStockRuleException
import de.partspicker.web.inventory.business.exceptions.InventoryItemMustBeUsableRuleException
import de.partspicker.web.inventory.business.exceptions.ItemAlreadyAssignedRuleException
import de.partspicker.web.inventory.business.objects.enums.InventoryItemCondition
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import de.partspicker.web.item.persistance.entities.ItemEntity
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity.IN_STOCK

data class AssignableItem(
    val itemId: Long,
    val itemTypeId: Long,
    val requiredAmount: Long,
    val assignedAmount: Long,
    val assignableToProjectId: Long,
    val assignableToProjectStatus: String?,
    val condition: InventoryItemCondition,
) {
    init {
        require(requiredAmount >= 0)
        require(assignedAmount >= 0)
        require(requiredAmount >= assignedAmount)
    }
    companion object {
        fun from(
            itemEntity: ItemEntity,
            assignedAmount: Long,
            requiredAmount: Long,
            assignableToProjectId: Long,
            assignableToProjectNodeName: String?,
        ): AssignableItem {
            // check business rules
            (itemEntity.assignedProject == null).elseThrow(
                ItemAlreadyAssignedRuleException(itemId = itemEntity.id)
            )

            (itemEntity.status == IN_STOCK).elseThrow(
                AssignableItemMustBeInStockRuleException(
                    itemId = itemEntity.id,
                    itemStatus = ItemStatus.from(itemEntity.status)
                )
            )

            val itemCondition = ItemCondition.from(itemEntity.condition)
            itemCondition.isUsable().elseThrow(
                InventoryItemMustBeUsableRuleException(itemEntity.id, itemCondition)
            )

            // create assignable item
            return AssignableItem(
                itemId = itemEntity.id,
                itemTypeId = itemEntity.type.id,
                requiredAmount = requiredAmount,
                assignedAmount = assignedAmount,
                assignableToProjectId = assignableToProjectId,
                assignableToProjectStatus = assignableToProjectNodeName,
                condition = InventoryItemCondition.from(itemEntity.condition),
            )
        }
    }
}
