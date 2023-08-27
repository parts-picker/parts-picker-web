package de.partspicker.web.workflow.business.automated.actions

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.inventory.business.InventoryItemService
import de.partspicker.web.inventory.business.objects.enums.CheckRequiredItemsResult
import de.partspicker.web.project.business.ProjectService
import de.partspicker.web.workflow.business.automated.exceptions.InstanceNotRelatedToProjectException
import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.business.objects.InstanceValue
import de.partspicker.web.workflow.business.objects.enums.DisplayType
import org.springframework.stereotype.Component

@Component(RequiredItemsAssignedAction.NAME)
class RequiredItemsAssignedAction(
    private val inventoryItemService: InventoryItemService,
    private val projectService: ProjectService
) : AutomatedAction {
    companion object : LoggingUtil {
        const val NAME = "required_items_assigned_action"

        const val SUCCESSFUL_EDGE = "check_required_items_assigned->implementation"
        const val FAILED_EDGE = "check_required_items_assigned->planning"

        const val MISSING_ITEMS_MESSAGE = "Required items not fully assigned yet"
        const val NONE_REQUIRED_MESSAGE = "At least one item type must be required"

        val logger = logger()
    }

    override fun execute(instance: Instance, instanceValues: List<InstanceValue>): AutomatedActionResult {
        val projectId = this.projectService.readByInstanceId(instanceId = instance.id)?.id
            ?: throw InstanceNotRelatedToProjectException(instanceId = instance.id)
        val checkResult = this.inventoryItemService.checkRequiredItemsAssignedToProject(projectId = projectId)

        val message: String?
        val displayType: DisplayType
        val chosenEdgeName: String
        when (checkResult) {
            CheckRequiredItemsResult.ALL_ASSIGNED -> {
                chosenEdgeName = SUCCESSFUL_EDGE
                message = null
                displayType = DisplayType.DEFAULT
            }

            CheckRequiredItemsResult.MISSING -> {
                chosenEdgeName = FAILED_EDGE
                message = MISSING_ITEMS_MESSAGE
                displayType = DisplayType.WARN
            }

            CheckRequiredItemsResult.NO_REQUIRED -> {
                chosenEdgeName = FAILED_EDGE
                message = NONE_REQUIRED_MESSAGE
                displayType = DisplayType.WARN
            }
        }

        return AutomatedActionResult(
            chosenEdgeName = chosenEdgeName,
            message = message,
            displayType = displayType
        )
    }
}
