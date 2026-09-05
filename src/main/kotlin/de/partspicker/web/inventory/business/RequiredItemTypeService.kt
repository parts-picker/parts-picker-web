package de.partspicker.web.inventory.business

import de.partspicker.web.common.business.exceptions.CrossOrgUnitReferenceException
import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.inventory.business.objects.CreateOrUpdateRequiredItemType
import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.inventory.business.rules.RequiredItemTypeAmountNotSmallerAssignedRule
import de.partspicker.web.inventory.persistence.RequiredItemTypeRepository
import de.partspicker.web.inventory.persistence.embeddableids.RequiredItemTypeId
import de.partspicker.web.inventory.persistence.entities.RequiredItemTypeEntity
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.business.objects.Project
import de.partspicker.web.project.business.rules.ProjectActiveRule
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.workflow.business.WorkflowInteractionService
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
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
    private val itemRepository: ItemRepository,
    private val orgUnitAccessService: OrgUnitAccessService
) {
    fun createOrUpdate(requiredItemTypeToUpdate: CreateOrUpdateRequiredItemType): RequiredItemType {
        val projectEntity = this.projectRepository.getNullableReferenceById(requiredItemTypeToUpdate.projectId)
            ?: throw ProjectNotFoundException(projectId = requiredItemTypeToUpdate.projectId)

        this.orgUnitAccessService.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.EDIT)

        val assignedAmount = this.inventoryItemReadService.countAssignedForItemTypeAndProject(
            projectId = requiredItemTypeToUpdate.projectId,
            itemTypeId = requiredItemTypeToUpdate.itemTypeId
        )
        RequiredItemTypeAmountNotSmallerAssignedRule(requiredItemTypeToUpdate.requiredAmount, assignedAmount).valid()

        val project = Project.from(projectEntity)
        NodeNameEqualsRule(project.status, "planning").valid()
        ProjectActiveRule(project).valid()

        val itemTypeEntity = this.itemTypeRepository.findByIdOrNull(requiredItemTypeToUpdate.itemTypeId)
            ?: throw ItemTypeNotFoundException(requiredItemTypeToUpdate.itemTypeId)

        (itemTypeEntity.orgUnit.id == projectEntity.orgUnit.id) elseThrow
            CrossOrgUnitReferenceException(projectEntity.orgUnit.id, itemTypeEntity.orgUnit.id)

        val createdRequiredItemType = this.requiredItemTypeRepository.save(
            RequiredItemTypeEntity(
                projectEntity = projectEntity,
                itemTypeEntity = itemTypeEntity,
                requiredAmount = requiredItemTypeToUpdate.requiredAmount
            )
        )

        return RequiredItemType.from(createdRequiredItemType, assignedAmount)
    }

    fun delete(projectId: Long, itemTypeId: Long) {
        val projectEntity = this.projectRepository.getNullableReferenceById(projectId)
            ?: throw ProjectNotFoundException(projectId = projectId)
        this.orgUnitAccessService.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.EDIT)

        val projectStatus = this.workflowInteractionService.readProjectStatus(projectId)
        NodeNameEqualsRule(projectStatus, "planning").valid()

        if (!this.itemTypeRepository.existsById(itemTypeId)) {
            throw ItemTypeNotFoundException(itemTypeId)
        }

        this.unassignAllItemsOfTypeFromProject(itemTypeId = itemTypeId, projectId = projectId)
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

    /**
     * Puts every item of the given type back into stock. Part of removing the requirement it belongs to,
     * which is the operation that carries the check.
     */
    private fun unassignAllItemsOfTypeFromProject(itemTypeId: Long, projectId: Long) {
        val itemsToUpdate = this.itemRepository.findAllByAssignedProjectIdAndTypeId(
            projectId = projectId,
            itemTypeId = itemTypeId,
            Pageable.unpaged()
        )

        val updatedItems = itemsToUpdate.map {
            it.assignedProject = null
            it.status = ItemStatusEntity.IN_STOCK
            it
        }

        this.itemRepository.saveAll(updatedItems)
    }
}
