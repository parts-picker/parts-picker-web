package de.partspicker.web.item.business.objects

import de.partspicker.web.item.api.requests.ItemTypePostRequest

data class CreateItemType(
    val orgUnitId: Long,
    val name: String,
    val description: String?
) {
    companion object {
        fun from(itemTypePostRequest: ItemTypePostRequest, orgUnitId: Long) = CreateItemType(
            orgUnitId = orgUnitId,
            name = itemTypePostRequest.name,
            description = itemTypePostRequest.description
        )
    }
}
