package de.partspicker.web.inventory.business.objects

import de.partspicker.web.inventory.api.requests.RequiredItemTypePostRequest
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

        fun from(requiredItemTypePostRequest: RequiredItemTypePostRequest, projectId: Long) = RequiredItemType(
            projectId = projectId,
            itemType = ItemType(requiredItemTypePostRequest.itemTypeId),
            requiredAmount = requiredItemTypePostRequest.requiredAmount
        )
    }

    object AsPage {
        fun from(pagedRequiredItemTypeEntities: Page<RequiredItemTypeEntity>) =
            pagedRequiredItemTypeEntities.map { from(it) }
    }

    init {
        require(requiredAmount > 0) { "The requiredAmount of a requiredItemType must be greater than zero" }
    }
}
