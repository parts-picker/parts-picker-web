package de.partspicker.web.inventory.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.util.withDefaultSort
import de.partspicker.web.inventory.business.exceptions.RequiredItemTypeNotFound
import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.inventory.persistence.RequiredItemTypeRepository
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream

@Service
class RequiredItemTypeReadService(
    private val requiredItemTypeRepository: RequiredItemTypeRepository,
    private val inventoryItemReadService: InventoryItemReadService,
    private val projectRepository: ProjectRepository,
    private val orgUnitAccessService: OrgUnitAccessService
) {
    companion object {
        const val DEFAULT_SORT = "itemType.name"
    }

    fun readByProjectIdAndItemTypeId(projectId: Long, itemTypeId: Long): RequiredItemType {
        this.requireReadAccessToProject(projectId)

        val requiredItemTypeEntity = this.requiredItemTypeRepository.findByProjectIdAndItemTypeId(projectId, itemTypeId)
            .orElseThrow { RequiredItemTypeNotFound(projectId, itemTypeId) }

        val assignedAmount = this.inventoryItemReadService.countAssignedForItemTypeAndProject(
            projectId = requiredItemTypeEntity.id.projectId,
            itemTypeId = requiredItemTypeEntity.id.itemTypeId
        )

        return RequiredItemType.from(requiredItemTypeEntity, assignedAmount)
    }

    fun readAllByProjectId(projectId: Long, pageable: Pageable): Page<RequiredItemType> {
        this.requireReadAccessToProject(projectId)

        val modifiedPageable = pageable.withDefaultSort(Sort.by(DEFAULT_SORT))

        return this.requiredItemTypeRepository.findAllByProjectId(projectId, modifiedPageable).map {
            val assignedAmount = this.inventoryItemReadService.countAssignedForItemTypeAndProject(
                projectId = it.id.projectId,
                itemTypeId = it.id.itemTypeId
            )

            RequiredItemType.from(it, assignedAmount)
        }
    }

    @Transactional(readOnly = true)
    fun streamAllByProjectId(projectId: Long): Stream<RequiredItemType> {
        this.requireReadAccessToProject(projectId)

        return this.requiredItemTypeRepository.streamAllByProjectId(projectId).map {
            val assignedAmount = this.inventoryItemReadService.countAssignedForItemTypeAndProject(
                projectId = it.id.projectId,
                itemTypeId = it.id.itemTypeId
            )

            RequiredItemType.from(it, assignedAmount)
        }
    }

    private fun requireReadAccessToProject(projectId: Long) {
        val projectEntity = this.projectRepository.getNullableReferenceById(projectId)
            ?: throw ProjectNotFoundException(projectId = projectId)

        this.orgUnitAccessService.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.READ)
    }
}
