package de.partspicker.web.item.business.objects

import de.partspicker.web.item.api.requests.ItemPostRequest
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import de.partspicker.web.item.persistance.entities.ItemEntity
import org.springframework.data.domain.Page

data class Item(
    val id: Long = 0,
    val type: ItemType,
    val status: ItemStatus,
    val condition: ItemCondition,
    val note: String?
) {
    companion object {
        fun from(itemEntity: ItemEntity) = Item(
            id = itemEntity.id,
            type = ItemType.from(itemEntity.type),
            status = ItemStatus.from(itemEntity.status),
            condition = ItemCondition.from(itemEntity.condition),
            note = itemEntity.note
        )

        fun from(itemPostRequest: ItemPostRequest) = Item(
            id = 0,
            type = ItemType(id = itemPostRequest.typeId),
            status = ItemStatus.from(itemPostRequest.status),
            condition = ItemCondition.from(itemPostRequest.condition),
            note = itemPostRequest.note
        )
    }

    object AsList {
        fun from(itemEntities: Iterable<ItemEntity>) = itemEntities.map { from(it) }
    }

    object AsPage {
        fun from(pagedItemEntities: Page<ItemEntity>) = pagedItemEntities.map { from(it) }
    }
}
