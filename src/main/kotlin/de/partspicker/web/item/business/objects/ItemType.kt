package de.partspicker.web.item.business.objects

import de.partspicker.web.item.persistance.entities.ItemTypeEntity

data class ItemType(
    val id: Long? = null,
    val name: String,
    val description: String
) {
    companion object {
        fun from(itemTypeEntity: ItemTypeEntity) = ItemType(
            id = itemTypeEntity.id!!,
            name = itemTypeEntity.name,
            description = itemTypeEntity.description
        )
    }

    object AsList {
        fun from(itemTypeEntities: Iterable<ItemTypeEntity>) = itemTypeEntities.map { from(it) }
    }
}
