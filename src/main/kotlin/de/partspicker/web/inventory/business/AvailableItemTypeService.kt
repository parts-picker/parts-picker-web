package de.partspicker.web.inventory.business

import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.inventory.business.objects.AvailableItemType
import de.partspicker.web.inventory.persistence.AvailableItemTypeSearchRepository
import de.partspicker.web.project.business.ProjectService
import de.partspicker.web.project.business.rules.ProjectActiveRule
import org.springframework.stereotype.Service

@Service
class AvailableItemTypeService(
    private val availableItemTypeSearchRepository: AvailableItemTypeSearchRepository,
    private val projectService: ProjectService
) {
    fun searchByName(queryName: String, projectId: Long): List<AvailableItemType> {
        val project = this.projectService.read(projectId)

        NodeNameEqualsRule(project.status, "planning").valid()
        ProjectActiveRule(project).valid()

        return AvailableItemType.AsList.from(
            this.availableItemTypeSearchRepository.searchByNameFilterRequired(queryName, projectId),
            projectId,
            project.status
        )
    }
}
