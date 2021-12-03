package de.partspicker.web.item.business.objects.enums

import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity

enum class ItemCondition {
    WRAPPED,
    NEW,
    USED,
    REPAIRABLE,
    BROKEN,
    UNKNOWN;

    companion object {
        fun from(itemConditionEntity: ItemConditionEntity) = when (itemConditionEntity) {
            ItemConditionEntity.WRAPPED -> WRAPPED
            ItemConditionEntity.NEW -> NEW
            ItemConditionEntity.USED -> USED
            ItemConditionEntity.REPAIRABLE -> REPAIRABLE
            ItemConditionEntity.BROKEN -> BROKEN
            ItemConditionEntity.UNKNOWN -> UNKNOWN
        }
    }
}
