package de.partspicker.web.item.persistance.entities.enums

import de.partspicker.web.item.business.objects.enums.ItemCondition

enum class ItemConditionEntity {
    WRAPPED,
    NEW,
    USED,
    REPAIRABLE,
    BROKEN,
    UNKNOWN;

    companion object {
        fun from(itemCondition: ItemCondition) = when (itemCondition) {
            ItemCondition.WRAPPED -> WRAPPED
            ItemCondition.NEW -> NEW
            ItemCondition.USED -> USED
            ItemCondition.REPAIRABLE -> REPAIRABLE
            ItemCondition.BROKEN -> BROKEN
            ItemCondition.UNKNOWN -> UNKNOWN
        }
    }
}
