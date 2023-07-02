package de.partspicker.web.inventory.business.objects

import de.partspicker.web.inventory.api.requests.RequiredItemTypePatchRequest
import de.partspicker.web.inventory.api.requests.RequiredItemTypePostRequest

data class CreateOrUpdateRequiredItemType(
    val projectId: Long,
    val itemTypeId: Long,
    val requiredAmount: Long
) {

    companion object {
        fun from(
            requiredItemTypePostRequest: RequiredItemTypePostRequest,
            projectId: Long,
            itemTypeId: Long
        ) = CreateOrUpdateRequiredItemType(
            projectId = projectId,
            itemTypeId = itemTypeId,
            requiredAmount = requiredItemTypePostRequest.requiredAmount
        )

        fun from(
            requiredItemTypePatchRequest: RequiredItemTypePatchRequest,
            projectId: Long,
            itemTypeId: Long
        ) = CreateOrUpdateRequiredItemType(
            projectId = projectId,
            itemTypeId = itemTypeId,
            requiredAmount = requiredItemTypePatchRequest.requiredAmount
        )
    }

    init {
        require(requiredAmount > 0) {
            "The requiredAmount of a CreateOrUpdateRequiredItemType must be greater than zero"
        }
    }
}
