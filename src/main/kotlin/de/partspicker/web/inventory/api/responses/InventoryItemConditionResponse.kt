package de.partspicker.web.inventory.api.responses

import de.partspicker.web.inventory.business.objects.enums.InventoryItemCondition

enum class InventoryItemConditionResponse {
    WRAPPED,
    NEW,
    USED;

    companion object {
        fun from(itemCondition: InventoryItemCondition) = when (itemCondition) {
            InventoryItemCondition.WRAPPED -> WRAPPED
            InventoryItemCondition.NEW -> NEW
            InventoryItemCondition.USED -> USED
        }
    }
}
