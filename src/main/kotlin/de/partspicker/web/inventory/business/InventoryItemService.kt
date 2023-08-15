package de.partspicker.web.inventory.business

import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.inventory.business.objects.AssignableItem
import de.partspicker.web.inventory.business.objects.AssignedItem
import de.partspicker.web.inventory.business.rules.RequiredGreaterAssignedAmountRule
import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.workflow.business.WorkflowInteractionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class InventoryItemService(
    private val itemRepository: ItemRepository,
    private val projectRepository: ProjectRepository,
    private val requiredItemTypeReadService: RequiredItemTypeReadService,
    private val workflowInteractionService: WorkflowInteractionService,
) {
    companion object {
        const val PLANNING_STATUS = "planning"
    }

    fun readAllAssignableForItemTypeAndProject(
        itemTypeId: Long,
        projectId: Long,
        pageable: Pageable,
    ): Page<AssignableItem> {
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
        return AssignedItem.AsPage.from(
            this.itemRepository.findAllByAssignedProjectIdAndTypeId(
                projectId,
                itemTypeId,
                pageable,
            ),
        )
    }

    fun assignToProject(itemId: Long, newProjectId: Long): AssignedItem {
        // check if node name is planning
        val currentNodeName = this.workflowInteractionService.readProjectStatus(newProjectId)
        NodeNameEqualsRule(currentNodeName, PLANNING_STATUS).valid()

        val itemToUpdate = this.itemRepository.findById(itemId).orElseThrow { ItemNotFoundException(itemId) }
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

        // check if item has project assigned & condition is usable
        val assignedItem = AssignedItem.from(itemToUpdate)

        val currentNodeName = this.workflowInteractionService.readProjectStatus(assignedItem.projectId)
        NodeNameEqualsRule(currentNodeName, PLANNING_STATUS).valid()

        itemToUpdate.assignedProject = null
        itemToUpdate.status = ItemStatusEntity.IN_STOCK

        val itemEntity = this.itemRepository.save(itemToUpdate)

        return Item.from(itemEntity)
    }

    fun removeAllWithTypeFromProject(
        itemTypeId: Long,
        projectId: Long,
    ) {
        val itemsToUpdate =
            this.itemRepository.findAllByAssignedProjectIdAndTypeId(
                projectId = projectId,
                itemTypeId = itemTypeId,
                Pageable.unpaged()
            )

        val currentNodeName = this.workflowInteractionService.readProjectStatus(projectId)
        NodeNameEqualsRule(currentNodeName, PLANNING_STATUS).valid()

        val updatedItems = itemsToUpdate.map {
            it.assignedProject = null
            it.status = ItemStatusEntity.IN_STOCK
            it
        }

        this.itemRepository.saveAll(updatedItems)
    }
}
