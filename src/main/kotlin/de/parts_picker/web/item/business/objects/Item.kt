package de.parts_picker.web.item.business.objects

import de.parts_picker.web.item.business.objects.enums.ItemCondition
import de.parts_picker.web.item.business.objects.enums.ItemStatus
import de.parts_picker.web.item.persistance.entities.ItemEntity

data class Item(
    val id: Long? = null,
    val status: ItemStatus,
    val condition: ItemCondition,
    val note: String?
) {
    companion object {
        fun from(itemEntity: ItemEntity) = Item(
            id = itemEntity.id,
            status = ItemStatus.from(itemEntity.status),
            condition = ItemCondition.from(itemEntity.condition),
            note = itemEntity.note
        )
    }

    object List {
        fun from(itemEntities: Iterable<ItemEntity>) = itemEntities.map { from(it) }
    }
}
