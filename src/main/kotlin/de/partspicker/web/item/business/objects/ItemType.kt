package de.partspicker.web.item.business.objects

import de.partspicker.web.item.api.requests.ItemTypePostRequest
import de.partspicker.web.item.api.requests.ItemTypePutRequest
import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import org.springframework.data.domain.Page

data class ItemType(
    val id: Long = 0,
    val name: String? = null,
    val description: String? = null
) {
    companion object {
        fun from(itemTypeEntity: ItemTypeEntity) = ItemType(
            id = itemTypeEntity.id,
            name = itemTypeEntity.name,
            description = itemTypeEntity.description
        )

        fun from(itemTypePostRequest: ItemTypePostRequest) = ItemType(
            id = 0,
            name = itemTypePostRequest.name,
            description = itemTypePostRequest.description
        )

        fun from(itemTypePutRequest: ItemTypePutRequest, id: Long) = ItemType(
            id = id,
            name = itemTypePutRequest.name,
            description = itemTypePutRequest.description
        )
    }

    object AsList {
        fun from(itemTypeEntities: Iterable<ItemTypeEntity>) = itemTypeEntities.map { from(it) }
    }

    object AsPage {
        fun from(pagedItemTypeEntities: Page<ItemTypeEntity>) = pagedItemTypeEntities.map { from(it) }
    }
}
