package de.partspicker.web.item.business.objects

import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import de.partspicker.web.item.persistance.entities.ItemEntity
import org.springframework.data.domain.Page

data class Item(
    val id: Long = 0,
    val type: ItemType,
    val assignedProjectId: Long?,
    val status: ItemStatus,
    val condition: ItemCondition,
    val note: String?,
    val orgUnitId: Long
) {
    companion object {
        fun from(itemEntity: ItemEntity) = Item(
            id = itemEntity.id,
            type = ItemType.from(itemEntity.type),
            assignedProjectId = itemEntity.assignedProject?.id,
            status = ItemStatus.from(itemEntity.status),
            condition = ItemCondition.from(itemEntity.condition),
            note = itemEntity.note,
            orgUnitId = itemEntity.orgUnit.id
        )
    }

    object AsList {
        fun from(itemEntities: Iterable<ItemEntity>) = itemEntities.map { from(it) }
    }

    object AsPage {
        fun from(pagedItemEntities: Page<ItemEntity>) = pagedItemEntities.map { from(it) }
    }
}
