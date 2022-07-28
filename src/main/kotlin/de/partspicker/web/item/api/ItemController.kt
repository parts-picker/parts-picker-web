package de.partspicker.web.item.api

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.item.api.requests.ItemPostRequest
import de.partspicker.web.item.api.requests.ItemPutRequest
import de.partspicker.web.item.api.resources.ItemResource
import de.partspicker.web.item.api.resources.ItemResourceAssembler
import de.partspicker.web.item.business.ItemService
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.business.objects.enums.ItemCondition
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ItemController(
    private val itemService: ItemService,
    private val pagedResourcesAssembler: PagedResourcesAssembler<Item>,
    private val itemResourceAssembler: ItemResourceAssembler,
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @PostMapping("/item-types/{itemTypeId}/items")
    fun handlePostItem(
        @PathVariable itemTypeId: Long,
        @RequestBody body: ItemPostRequest
    ): ResponseEntity<ItemResource> {
        logger.info("=> POST request for new item")

        val createdItem = this.itemService.create(Item.from(body, itemTypeId))

        return ResponseEntity(itemResourceAssembler.toModel(createdItem), HttpStatus.OK)
    }

    @GetMapping("/items")
    fun handleGetAllItems(pageable: Pageable): ResponseEntity<PagedModel<ItemResource>> {
        logger.info("=> GET request for all items")

        val items = this.itemService.getItems(pageable)
        val pagedResource = this.pagedResourcesAssembler.toModel(items, itemResourceAssembler)

        return ResponseEntity(pagedResource, HttpStatus.OK)
    }

    @GetMapping("/items/{id}")
    fun handleGetItemById(@PathVariable id: Long): ResponseEntity<ItemResource> {
        logger.info("=> GET request for item with id $id")

        val itemResource = itemResourceAssembler.toModel(this.itemService.getItemById(id))

        return ResponseEntity(itemResource, HttpStatus.OK)
    }

    @GetMapping("/item-types/{id}/items")
    fun handleGetItemsByItemTypeId(
        @PathVariable id: Long,
        pageable: Pageable
    ): ResponseEntity<PagedModel<ItemResource>> {
        logger.info("=> GET request for all items with itemTypeId $id")

        val pagedItems = this.itemService.getItemsForItemType(id, pageable)
        val itemResources = this.pagedResourcesAssembler.toModel(pagedItems, itemResourceAssembler)

        return ResponseEntity(itemResources, HttpStatus.OK)
    }

    @PutMapping("items/{id}")
    fun handlePutItemById(@PathVariable id: Long, @RequestBody body: ItemPutRequest): ResponseEntity<ItemResource> {
        logger.info("=> PUT request for item with id $id")

        val updatedItem = this.itemService.update(
            id,
            ItemCondition.from(body.condition),
            body.note
        )
        val itemResource = itemResourceAssembler.toModel(updatedItem)

        return ResponseEntity(itemResource, HttpStatus.OK)
    }

    @DeleteMapping("/items/{id}")
    fun handleDeleteItemById(@PathVariable id: Long): ResponseEntity<Unit> {
        logger.info("=> DELETE request for item with id $id")

        this.itemService.delete(id)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
