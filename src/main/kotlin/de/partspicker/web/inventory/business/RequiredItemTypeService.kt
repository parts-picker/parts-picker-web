package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.inventory.persitance.RequiredItemTypeRepository
import de.partspicker.web.inventory.persitance.entities.RequiredItemTypeEntity
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RequiredItemTypeService(
    private val requiredItemTypeRepository: RequiredItemTypeRepository,
    private val projectRepository: ProjectRepository,
    private val itemTypeRepository: ItemTypeRepository
) {
    fun create(requiredItemType: RequiredItemType): RequiredItemType {
        if (!this.projectRepository.existsById(requiredItemType.projectId)) {
            throw ProjectNotFoundException(projectId = requiredItemType.projectId)
        }

        if (!this.itemTypeRepository.existsById(requiredItemType.itemType.id)) {
            throw ItemTypeNotFoundException(requiredItemType.itemType.id)
        }

        val createdRequiredItemType = this.requiredItemTypeRepository.save(
            RequiredItemTypeEntity.from(requiredItemType)
        )

        return RequiredItemType.from(createdRequiredItemType)
    }

    fun readAllByProjectId(projectId: Long, pageable: Pageable): Page<RequiredItemType> {
        return RequiredItemType.AsPage.from(this.requiredItemTypeRepository.findAllByProjectId(projectId, pageable))
    }
}
