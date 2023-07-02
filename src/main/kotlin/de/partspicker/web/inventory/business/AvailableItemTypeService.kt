package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.business.objects.AvailableItemType
import de.partspicker.web.inventory.persistence.AvailableItemTypeSearchRepository
import de.partspicker.web.workflow.business.WorkflowInteractionService
import org.springframework.stereotype.Service

@Service
class AvailableItemTypeService(
    private val availableItemTypeSearchRepository: AvailableItemTypeSearchRepository,
    private val workflowInteractionService: WorkflowInteractionService
) {
    fun searchByName(queryName: String, projectId: Long): List<AvailableItemType> {
        val projectStatus = this.workflowInteractionService.readProjectStatus(projectId)

        return AvailableItemType.AsList.from(
            this.availableItemTypeSearchRepository.searchByNameFilterRequired(queryName, projectId),
            projectId,
            projectStatus
        )
    }
}
