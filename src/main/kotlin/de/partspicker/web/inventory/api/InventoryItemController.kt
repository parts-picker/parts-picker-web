package de.partspicker.web.inventory.api

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.inventory.api.resources.AssignableItemResource
import de.partspicker.web.inventory.api.resources.AssignableItemResourceAssembler
import de.partspicker.web.inventory.api.resources.AssignedItemResource
import de.partspicker.web.inventory.api.resources.AssignedItemResourceAssembler
import de.partspicker.web.inventory.business.InventoryItemService
import de.partspicker.web.inventory.business.objects.AssignableItem
import de.partspicker.web.inventory.business.objects.AssignedItem
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryItemController(
    private val inventoryItemService: InventoryItemService,
    private val assignedItemResourceAssembler: AssignedItemResourceAssembler,
    private val assignableItemResourceAssembler: AssignableItemResourceAssembler,
    private val assignedPagedResourcesAssembler: PagedResourcesAssembler<AssignedItem>,
    private val assignablePageResourcesAssembler: PagedResourcesAssembler<AssignableItem>
) {

    companion object : LoggingUtil {
        val logger = logger()
    }

    @GetMapping("/projects/{projectId}/required/{itemTypeId}/assignable")
    fun handleGetAllAssignableItems(
        @PathVariable projectId: Long,
        @PathVariable itemTypeId: Long,
        pageable: Pageable
    ): ResponseEntity<PagedModel<AssignableItemResource>> {
        logger.info(
            "=> GET request for fetching all assignable items of item type with id '$itemTypeId'" +
                " for project with id '$projectId'"
        )

        val pagedAssignableItems = this.inventoryItemService.readAllAssignableForItemTypeAndProject(
            itemTypeId = itemTypeId,
            projectId = projectId,
            pageable = pageable
        )
        val assignableItemResources = this.assignablePageResourcesAssembler.toModel(
            pagedAssignableItems,
            assignableItemResourceAssembler
        )

        return ResponseEntity(assignableItemResources, HttpStatus.OK)
    }

    @GetMapping("/projects/{projectId}/required/{itemTypeId}/assigned")
    fun handleGetAllAssignedItems(
        @PathVariable projectId: Long,
        @PathVariable itemTypeId: Long,
        pageable: Pageable
    ): ResponseEntity<PagedModel<AssignedItemResource>> {
        logger.info(
            "=> GET request for fetching all assigned items of item type with id '$itemTypeId'" +
                " for project with id '$projectId'"
        )

        val pagedAssignedItems = this.inventoryItemService.readAllAssignedForItemTypeAndProject(
            itemTypeId = itemTypeId,
            projectId = projectId,
            pageable = pageable
        )
        val assignedItemResources = this.assignedPagedResourcesAssembler.toModel(
            pagedAssignedItems,
            assignedItemResourceAssembler
        )

        return ResponseEntity(assignedItemResources, HttpStatus.OK)
    }

    @PatchMapping("/items/{itemId}/assigned/{projectId}")
    fun handlePatchAssignableItem(
        @PathVariable projectId: Long,
        @PathVariable itemId: Long
    ): ResponseEntity<Unit> {
        logger.info("=> PATCH request for assigning item with id '$itemId' to project with id '$projectId'")

        this.inventoryItemService.assignToProject(itemId, projectId)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PatchMapping("/items/{itemId}/assignable")
    fun handlePatchAssignedItem(
        @PathVariable itemId: Long
    ): ResponseEntity<Unit> {
        logger.info("=> PATCH request for removing assigned item with id '$itemId' from its project'")

        this.inventoryItemService.removeFromProject(itemId)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
