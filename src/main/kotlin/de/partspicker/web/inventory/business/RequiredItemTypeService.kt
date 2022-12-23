package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.inventory.persitance.RequiredItemTypeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RequiredItemTypeService(
    private val requiredItemTypeRepository: RequiredItemTypeRepository
) {
    fun readAllByProjectId(projectId: Long, pageable: Pageable): Page<RequiredItemType> {
        return RequiredItemType.AsPage.from(this.requiredItemTypeRepository.findAllByProjectId(projectId, pageable))
    }
}
