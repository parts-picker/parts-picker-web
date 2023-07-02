package de.partspicker.web.inventory.business.objects.enums

import de.partspicker.web.inventory.business.exceptions.InvalidEnumConversionException
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity

enum class InventoryItemCondition {
    WRAPPED,
    NEW,
    USED;

    companion object {
        fun from(itemCondition: ItemConditionEntity) = when (itemCondition) {
            ItemConditionEntity.WRAPPED -> WRAPPED
            ItemConditionEntity.NEW -> NEW
            ItemConditionEntity.USED -> USED
            else -> throw InvalidEnumConversionException(itemCondition.name)
        }
    }
}
