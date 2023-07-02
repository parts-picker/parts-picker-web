package de.partspicker.web.inventory.business.objects

import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.inventory.business.exceptions.AssignedItemMissingProjectRuleException
import de.partspicker.web.inventory.business.exceptions.InventoryItemMustBeUsableRuleException
import de.partspicker.web.inventory.business.objects.enums.InventoryItemCondition
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.persistance.entities.ItemEntity
import org.springframework.data.domain.Page

data class AssignedItem(
    val itemId: Long,
    val itemTypeId: Long,
    val projectId: Long,
    val projectStatus: String,
    val condition: InventoryItemCondition
) {
    init {
        require(projectStatus.isNotBlank())
    }

    companion object {
        fun from(itemEntity: ItemEntity): AssignedItem {
            // check business rules
            (itemEntity.assignedProject != null).elseThrow(
                AssignedItemMissingProjectRuleException(itemEntity.id)
            )

            val itemCondition = ItemCondition.from(itemEntity.condition)
            itemCondition.isUsable().elseThrow(
                InventoryItemMustBeUsableRuleException(itemEntity.id, itemCondition)
            )

            return AssignedItem(
                itemId = itemEntity.id,
                itemTypeId = itemEntity.type.id,
                projectId = itemEntity.assignedProject!!.id,
                condition = InventoryItemCondition.from(itemEntity.condition),
                projectStatus = itemEntity.assignedProject!!.workflowInstance?.currentNode!!.name
            )
        }
    }

    object AsPage {
        fun from(itemEntities: Page<ItemEntity>) = itemEntities.map { from(it) }
    }
}
