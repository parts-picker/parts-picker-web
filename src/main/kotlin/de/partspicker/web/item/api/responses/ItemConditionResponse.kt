package de.partspicker.web.item.api.responses

import de.partspicker.web.item.business.objects.enums.ItemCondition

enum class ItemConditionResponse {
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
