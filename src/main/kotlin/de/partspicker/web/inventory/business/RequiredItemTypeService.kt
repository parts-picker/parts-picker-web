package de.partspicker.web.inventory.business

import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.inventory.business.objects.CreateOrUpdateRequiredItemType
import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.inventory.business.rules.RequiredItemTypeAmountNotSmallerAssignedRule
import de.partspicker.web.inventory.persistence.RequiredItemTypeRepository
import de.partspicker.web.inventory.persistence.embeddableids.RequiredItemTypeId
import de.partspicker.web.inventory.persistence.entities.RequiredItemTypeEntity
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.workflow.business.WorkflowInteractionService
import org.springframework.stereotype.Service

@Service
class RequiredItemTypeService(
    private val requiredItemTypeRepository: RequiredItemTypeRepository,
    private val projectRepository: ProjectRepository,
    private val itemTypeRepository: ItemTypeRepository,
    private val workflowInteractionService: WorkflowInteractionService,
    private val inventoryItemReadService: InventoryItemReadService,
    private val inventoryItemService: InventoryItemService
) {
    fun createOrUpdate(requiredItemType: CreateOrUpdateRequiredItemType): RequiredItemType {
        if (!this.projectRepository.existsById(requiredItemType.projectId)) {
            throw ProjectNotFoundException(projectId = requiredItemType.projectId)
        }

        val assignedAmount = this.inventoryItemReadService.countAssignedForItemTypeAndProject(
            projectId = requiredItemType.projectId,
            itemTypeId = requiredItemType.itemTypeId
        )
        RequiredItemTypeAmountNotSmallerAssignedRule(requiredItemType.requiredAmount, assignedAmount).valid()

        val projectStatus = this.workflowInteractionService.readProjectStatus(requiredItemType.projectId)
        NodeNameEqualsRule(projectStatus, "planning").valid()

        if (!this.itemTypeRepository.existsById(requiredItemType.itemTypeId)) {
            throw ItemTypeNotFoundException(requiredItemType.itemTypeId)
        }

        val createdRequiredItemType = this.requiredItemTypeRepository.save(
            RequiredItemTypeEntity.from(requiredItemType),
        )

        return RequiredItemType.from(createdRequiredItemType, assignedAmount)
    }

    fun delete(projectId: Long, itemTypeId: Long) {
        if (!this.projectRepository.existsById(projectId)) {
            throw ProjectNotFoundException(projectId = projectId)
        }

        val currentNodeName = this.workflowInteractionService.readProjectStatus(projectId)
        NodeNameEqualsRule(currentNodeName, "planning").valid()

        if (!this.itemTypeRepository.existsById(itemTypeId)) {
            throw ItemTypeNotFoundException(itemTypeId)
        }

        this.inventoryItemService.removeAllWithTypeFromProject(itemTypeId = itemTypeId, projectId = projectId)
        this.requiredItemTypeRepository.deleteById(RequiredItemTypeId(projectId, itemTypeId))
    }
}
