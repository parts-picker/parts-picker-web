package de.partspicker.web.item.business.objects

import de.partspicker.web.item.api.requests.ItemTypePostRequest

data class CreateItemType(
    val name: String,
    val description: String?
) {
    companion object {
        fun from(itemTypePostRequest: ItemTypePostRequest) = CreateItemType(
            name = itemTypePostRequest.name,
            description = itemTypePostRequest.description
        )
    }
}
