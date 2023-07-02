package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.business.exceptions.RequiredItemTypeNotFound
import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.inventory.persistence.RequiredItemTypeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RequiredItemTypeReadService(
    private val requiredItemTypeRepository: RequiredItemTypeRepository,
    private val inventoryItemReadService: InventoryItemReadService
) {
    fun readByProjectIdAndItemTypeId(projectId: Long, itemTypeId: Long): RequiredItemType {
        val requiredItemTypeEntity = this.requiredItemTypeRepository.findByProjectIdAndItemTypeId(projectId, itemTypeId)
            .orElseThrow { RequiredItemTypeNotFound(projectId, itemTypeId) }

        val assignedAmount = this.inventoryItemReadService.countAssignedForItemTypeAndProject(
            projectId = requiredItemTypeEntity.id.projectId,
            itemTypeId = requiredItemTypeEntity.id.itemTypeId
        )

        return RequiredItemType.from(requiredItemTypeEntity, assignedAmount)
    }

    fun readAllByProjectId(projectId: Long, pageable: Pageable): Page<RequiredItemType> {
        return this.requiredItemTypeRepository.findAllByProjectId(projectId, pageable).map {
            val assignedAmount = this.inventoryItemReadService.countAssignedForItemTypeAndProject(
                projectId = it.id.projectId,
                itemTypeId = it.id.itemTypeId
            )

            RequiredItemType.from(it, assignedAmount)
        }
    }
}
