package de.partspicker.web.inventory.business

import de.partspicker.web.common.business.exceptions.CrossOrgUnitReferenceException
import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.inventory.business.objects.AssignableItem
import de.partspicker.web.inventory.business.objects.AssignedItem
import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.inventory.business.objects.enums.CheckRequiredItemsResult
import de.partspicker.web.inventory.business.rules.RequiredGreaterAssignedAmountRule
import de.partspicker.web.inventory.persistence.RequiredItemTypeRepository
import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.workflow.business.WorkflowInteractionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Suppress("LongParameterList")
@Service
class InventoryItemService(
    private val itemRepository: ItemRepository,
    private val projectRepository: ProjectRepository,
    private val requiredItemTypeReadService: RequiredItemTypeReadService,
    private val requiredItemTypeRepository: RequiredItemTypeRepository,
    private val inventoryItemReadService: InventoryItemReadService,
    private val workflowInteractionService: WorkflowInteractionService,
    private val itemTypeRepository: ItemTypeRepository,
    private val orgUnitAccessService: OrgUnitAccessService,
) {
    companion object {
        const val PLANNING_STATUS = "planning"
    }

    fun readAllAssignableForItemTypeAndProject(
        itemTypeId: Long,
        projectId: Long,
        pageable: Pageable,
    ): Page<AssignableItem> {
        this.requireAccessToBoth(projectId, itemTypeId, AccessLevel.READ)

        val itemEntities = this.itemRepository.findAllAssignableByTypeId(itemTypeId, pageable)

        // additional info
        val requiredItemTypeEntity =
            this.requiredItemTypeReadService.readByProjectIdAndItemTypeId(projectId, itemTypeId)
        val assignedItemsAmount = this.itemRepository.countByAssignedProjectIdAndTypeId(projectId, itemTypeId)
        val currentNodeName = this.workflowInteractionService.readProjectStatus(projectId)

        return itemEntities.map { itemEntity ->
            AssignableItem.from(
                itemEntity = itemEntity,
                assignedAmount = assignedItemsAmount,
                requiredAmount = requiredItemTypeEntity.requiredAmount,
                assignableToProjectId = projectId,
                assignableToProjectNodeName = currentNodeName
            )
        }
    }

    fun readAllAssignedForItemTypeAndProject(
        itemTypeId: Long,
        projectId: Long,
        pageable: Pageable,
    ): Page<AssignedItem> {
        this.requireAccessToBoth(projectId, itemTypeId, AccessLevel.READ)

        return AssignedItem.AsPage.from(
            this.itemRepository.findAllByAssignedProjectIdAndTypeId(
                projectId,
                itemTypeId,
                pageable,
            ),
        )
    }

    /**
     * Answers whether a project has everything it requires. Called by automated workflow actions, which act
     * for the system & have no user to check, so it reads through the repository rather than a checked service.
     */
    fun checkRequiredItemsAssignedToProject(projectId: Long): CheckRequiredItemsResult {
        val requiredItemTypes = this.requiredItemTypeRepository
            .findAllByProjectId(projectId, Pageable.unpaged())
            .map { requiredItemTypeEntity ->
                RequiredItemType.from(
                    requiredItemTypeEntity,
                    this.inventoryItemReadService.countAssignedForItemTypeAndProject(
                        projectId = requiredItemTypeEntity.id.projectId,
                        itemTypeId = requiredItemTypeEntity.id.itemTypeId
                    )
                )
            }

        val allAssigned = requiredItemTypes.all { it.isRequiredAmountAssigned() }
        return when {
            requiredItemTypes.isEmpty -> CheckRequiredItemsResult.NO_REQUIRED
            allAssigned -> CheckRequiredItemsResult.ALL_ASSIGNED
            else -> CheckRequiredItemsResult.MISSING
        }
    }

    fun assignToProject(itemId: Long, newProjectId: Long): AssignedItem {
        val itemToUpdate = this.itemRepository.findById(itemId).orElseThrow { ItemNotFoundException(itemId) }
        this.orgUnitAccessService.requireAtLeast(itemToUpdate.orgUnit.id, AccessLevel.EDIT)
        this.requireSameOrgUnitAsProject(newProjectId, itemToUpdate.orgUnit.id)

        // check if node name is planning
        val currentNodeName = this.workflowInteractionService.readProjectStatus(newProjectId)
        NodeNameEqualsRule(currentNodeName, PLANNING_STATUS).valid()

        val requiredItemTypeEntity = this.requiredItemTypeReadService.readByProjectIdAndItemTypeId(
            newProjectId,
            itemToUpdate.type.id
        )
        val assignedItemsAmount = this.itemRepository.countByAssignedProjectIdAndTypeId(
            newProjectId,
            itemToUpdate.type.id
        )

        AssignableItem.from(
            itemEntity = itemToUpdate,
            assignedAmount = assignedItemsAmount,
            requiredAmount = requiredItemTypeEntity.requiredAmount,
            assignableToProjectId = newProjectId,
            assignableToProjectNodeName = currentNodeName
        )

        RequiredGreaterAssignedAmountRule(
            requiredAmount = requiredItemTypeEntity.requiredAmount,
            assignedAmount = assignedItemsAmount
        ).valid()

        this.projectRepository.existsById(newProjectId) elseThrow ProjectNotFoundException(projectId = newProjectId)
        itemToUpdate.assignedProject = this.projectRepository.getReferenceById(newProjectId)
        itemToUpdate.status = ItemStatusEntity.RESERVED

        val savedItem = this.itemRepository.save(itemToUpdate)

        return AssignedItem.from(itemEntity = savedItem)
    }

    fun removeFromProject(itemId: Long): Item {
        val itemToUpdate = this.itemRepository.findById(itemId).orElseThrow { ItemNotFoundException(itemId) }
        this.orgUnitAccessService.requireAtLeast(itemToUpdate.orgUnit.id, AccessLevel.EDIT)

        // check if item has project assigned & condition is usable
        val assignedItem = AssignedItem.from(itemToUpdate)

        val currentNodeName = this.workflowInteractionService.readProjectStatus(assignedItem.projectId)
        NodeNameEqualsRule(currentNodeName, PLANNING_STATUS).valid()

        itemToUpdate.assignedProject = null
        itemToUpdate.status = ItemStatusEntity.IN_STOCK

        val itemEntity = this.itemRepository.save(itemToUpdate)

        return Item.from(itemEntity)
    }

    private fun requireAccessToBoth(projectId: Long, itemTypeId: Long, requiredLevel: AccessLevel) {
        val projectEntity = this.projectRepository.getNullableReferenceById(projectId)
            ?: throw ProjectNotFoundException(projectId = projectId)
        this.orgUnitAccessService.requireAtLeast(projectEntity.orgUnit.id, requiredLevel)

        val itemTypeEntity = this.itemTypeRepository.findByIdOrNull(itemTypeId)
            ?: throw ItemTypeNotFoundException(itemTypeId)

        (itemTypeEntity.orgUnit.id == projectEntity.orgUnit.id) elseThrow
            CrossOrgUnitReferenceException(projectEntity.orgUnit.id, itemTypeEntity.orgUnit.id)
    }

    private fun requireSameOrgUnitAsProject(projectId: Long, orgUnitId: Long) {
        val projectEntity = this.projectRepository.getNullableReferenceById(projectId)
            ?: throw ProjectNotFoundException(projectId = projectId)

        (projectEntity.orgUnit.id == orgUnitId) elseThrow
            CrossOrgUnitReferenceException(orgUnitId, projectEntity.orgUnit.id)
    }
}
