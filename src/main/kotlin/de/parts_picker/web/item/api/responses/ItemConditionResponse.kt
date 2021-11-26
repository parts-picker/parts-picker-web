package de.parts_picker.web.item.api.responses

import de.parts_picker.web.item.business.objects.enums.ItemCondition

enum class ItemConditionResponse {
    WRAPPED,
    NEW,
    USED,
    REPAIRABLE,
    BROKEN,
    UNKNOWN;

    companion object {
        fun from(itemCondition: ItemCondition) = when(itemCondition) {
            ItemCondition.WRAPPED -> WRAPPED
            ItemCondition.NEW -> NEW
            ItemCondition.USED -> USED
            ItemCondition.REPAIRABLE -> REPAIRABLE
            ItemCondition.BROKEN -> BROKEN
            ItemCondition.UNKNOWN -> UNKNOWN
        }
    }

}