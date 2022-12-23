package de.partspicker.web.inventory.business.objects

import de.partspicker.web.inventory.persitance.entities.RequiredItemTypeEntity
import de.partspicker.web.item.business.objects.ItemType
import org.springframework.data.domain.Page

data class RequiredItemType(
    val projectId: Long,
    val itemType: ItemType,
    val requiredAmount: Long
) {
    companion object {
        fun from(requiredItemTypeEntity: RequiredItemTypeEntity) = RequiredItemType(
            projectId = requiredItemTypeEntity.project.id,
            itemType = ItemType.from(requiredItemTypeEntity.itemType),
            requiredAmount = requiredItemTypeEntity.requiredAmount
        )
    }

    object AsPage {
        fun from(pagedRequiredItemTypeEntities: Page<RequiredItemTypeEntity>) =
            pagedRequiredItemTypeEntities.map { from(it) }
    }
}
