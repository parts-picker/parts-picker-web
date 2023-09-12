package de.partspicker.web.workflow.business.automated.actions

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.project.business.ProjectService
import de.partspicker.web.workflow.business.automated.exceptions.InstanceNotRelatedToProjectException
import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.business.objects.InstanceValue
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component(ProjectCompletionInventoryUpdateAction.NAME)
class ProjectCompletionInventoryUpdateAction(
    private val projectService: ProjectService,
    private val itemRepository: ItemRepository
) : AutomatedAction {
    companion object : LoggingUtil {
        const val NAME = "project_completion_inventory_update"

        const val SUCCESSFUL_EDGE = "project_completion_inventory_update->successful_completion"

        val logger = logger()
    }

    override fun execute(instance: Instance, instanceValues: List<InstanceValue>): AutomatedActionResult {
        val projectId = this.projectService.readByInstanceId(instanceId = instance.id)?.id
            ?: throw InstanceNotRelatedToProjectException(instanceId = instance.id)

        logger.info("Updating inventory for project with id $projectId, setting status of all assigned items to used")

        this.itemRepository.updateSetStatusUsedByAssignedProjectId(projectId)

        return AutomatedActionResult(SUCCESSFUL_EDGE)
    }
}
