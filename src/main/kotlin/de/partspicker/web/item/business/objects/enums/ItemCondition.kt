package de.partspicker.web.item.business.objects.enums

import de.partspicker.web.item.api.requests.ItemConditionRequest
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity

enum class ItemCondition {
    WRAPPED,
    NEW,
    USED,
    REPAIRABLE,
    BROKEN,
    UNKNOWN;

    fun isUsable() =
        this == WRAPPED ||
            this == NEW ||
            this == USED

    companion object {
        fun from(itemConditionEntity: ItemConditionEntity) = when (itemConditionEntity) {
            ItemConditionEntity.WRAPPED -> WRAPPED
            ItemConditionEntity.NEW -> NEW
            ItemConditionEntity.USED -> USED
            ItemConditionEntity.REPAIRABLE -> REPAIRABLE
            ItemConditionEntity.BROKEN -> BROKEN
            ItemConditionEntity.UNKNOWN -> UNKNOWN
        }

        fun from(itemConditionRequest: ItemConditionRequest) = when (itemConditionRequest) {
            ItemConditionRequest.WRAPPED -> WRAPPED
            ItemConditionRequest.NEW -> NEW
            ItemConditionRequest.USED -> USED
            ItemConditionRequest.REPAIRABLE -> REPAIRABLE
            ItemConditionRequest.BROKEN -> BROKEN
            ItemConditionRequest.UNKNOWN -> UNKNOWN
        }
    }
}
