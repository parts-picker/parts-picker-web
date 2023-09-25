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
import de.partspicker.web.project.business.objects.Project
import de.partspicker.web.project.business.rules.ProjectActiveRule
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.workflow.business.WorkflowInteractionService
import org.springframework.stereotype.Service

@Suppress("LongParameterList")
@Service
class RequiredItemTypeService(
    private val requiredItemTypeRepository: RequiredItemTypeRepository,
    private val requiredItemTypeReadService: RequiredItemTypeReadService,
    private val projectRepository: ProjectRepository,
    private val itemTypeRepository: ItemTypeRepository,
    private val workflowInteractionService: WorkflowInteractionService,
    private val inventoryItemReadService: InventoryItemReadService,
    private val inventoryItemService: InventoryItemService
) {
    fun createOrUpdate(requiredItemTypeToUpdate: CreateOrUpdateRequiredItemType): RequiredItemType {
        val projectEntity = this.projectRepository.getNullableReferenceById(requiredItemTypeToUpdate.projectId)
            ?: throw ProjectNotFoundException(projectId = requiredItemTypeToUpdate.projectId)

        val assignedAmount = this.inventoryItemReadService.countAssignedForItemTypeAndProject(
            projectId = requiredItemTypeToUpdate.projectId,
            itemTypeId = requiredItemTypeToUpdate.itemTypeId
        )
        RequiredItemTypeAmountNotSmallerAssignedRule(requiredItemTypeToUpdate.requiredAmount, assignedAmount).valid()

        val project = Project.from(projectEntity)
        NodeNameEqualsRule(project.status, "planning").valid()
        ProjectActiveRule(project).valid()

        if (!this.itemTypeRepository.existsById(requiredItemTypeToUpdate.itemTypeId)) {
            throw ItemTypeNotFoundException(requiredItemTypeToUpdate.itemTypeId)
        }

        val createdRequiredItemType = this.requiredItemTypeRepository.save(
            RequiredItemTypeEntity(
                projectEntity = projectEntity,
                itemTypeId = requiredItemTypeToUpdate.itemTypeId,
                requiredAmount = requiredItemTypeToUpdate.requiredAmount
            )
        )

        return RequiredItemType.from(createdRequiredItemType, assignedAmount)
    }

    fun delete(projectId: Long, itemTypeId: Long) {
        if (!this.projectRepository.existsById(projectId)) {
            throw ProjectNotFoundException(projectId = projectId)
        }

        val projectStatus = this.workflowInteractionService.readProjectStatus(projectId)
        NodeNameEqualsRule(projectStatus, "planning").valid()

        if (!this.itemTypeRepository.existsById(itemTypeId)) {
            throw ItemTypeNotFoundException(itemTypeId)
        }

        this.inventoryItemService.removeAllWithTypeFromProject(itemTypeId = itemTypeId, projectId = projectId)
        this.requiredItemTypeRepository.deleteById(RequiredItemTypeId(projectId, itemTypeId))
    }

    fun deleteAllByProjectId(projectId: Long) {
        if (!this.projectRepository.existsById(projectId)) {
            throw ProjectNotFoundException(projectId = projectId)
        }

        this.requiredItemTypeRepository.deleteAllByProjectId(projectId)
    }

    fun copyAllToTargetProjectByProjectId(sourceProjectId: Long, targetProjectId: Long) {
        if (!this.projectRepository.existsById(sourceProjectId)) {
            throw ProjectNotFoundException(projectId = sourceProjectId)
        }

        if (!this.projectRepository.existsById(targetProjectId)) {
            throw ProjectNotFoundException(projectId = targetProjectId)
        }

        this.requiredItemTypeReadService.streamAllByProjectId(sourceProjectId)
            .map {
                CreateOrUpdateRequiredItemType(
                    projectId = targetProjectId,
                    itemTypeId = it.itemType.id,
                    requiredAmount = it.requiredAmount
                )
            }
            .forEach {
                this.createOrUpdate(it)
            }
    }
}
