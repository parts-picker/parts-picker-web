package de.partspicker.web.inventory.business

import de.partspicker.web.item.persistance.ItemRepository
import org.springframework.stereotype.Service

@Service
class InventoryItemReadService(
    private val itemRepository: ItemRepository
) {
    fun countAssignedForItemTypeAndProject(
        itemTypeId: Long,
        projectId: Long
    ): Long {
        return this.itemRepository.countByAssignedProjectIdAndTypeId(
            projectId = projectId,
            itemTypeId = itemTypeId
        )
    }
}
