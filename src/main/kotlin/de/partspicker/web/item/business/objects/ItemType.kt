package de.partspicker.web.item.business.objects

import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import org.springframework.data.domain.Page

data class ItemType(
    val id: Long = 0,
    val name: String? = null,
    val description: String? = null,
    val orgUnitId: Long
) {
    companion object {
        fun from(itemTypeEntity: ItemTypeEntity) = ItemType(
            id = itemTypeEntity.id,
            name = itemTypeEntity.name,
            description = itemTypeEntity.description,
            orgUnitId = itemTypeEntity.orgUnit.id
        )
    }

    object AsList {
        fun from(itemTypeEntities: Iterable<ItemTypeEntity>) = itemTypeEntities.map { from(it) }
    }

    object AsPage {
        fun from(pagedItemTypeEntities: Page<ItemTypeEntity>) = pagedItemTypeEntities.map { from(it) }
    }
}
