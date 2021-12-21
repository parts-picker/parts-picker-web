package de.partspicker.web.item.api

import de.partspicker.web.common.hal.withMethods
import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.item.api.resources.ItemResource
import de.partspicker.web.item.business.ItemService
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ItemController(
    private val itemService: ItemService
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @GetMapping("/items")
    fun handleGetAllItems(): ResponseEntity<Iterable<ItemResource>> {
        logger.info("=> GET request for all items")

        val itemResources = this.itemService.getItems().map { ItemResource.from(it) }

        val selfLink = linkTo<ItemController> { handleGetAllItems() }.withSelfRel()
        val collectionModel = CollectionModel.of(itemResources, selfLink)

        return ResponseEntity(collectionModel, HttpStatus.OK)
    }

    @GetMapping("/items/{id}")
    fun handleGetItemById(@PathVariable id: Long): ResponseEntity<ItemResource> {
        logger.info("=> GET request for item with id $id")

        val itemResource = ItemResource.from(this.itemService.getItemById(id))

        return ResponseEntity(itemResource, HttpStatus.OK)
    }

    @GetMapping("/item-types/{id}/items")
    fun handleGetItemsByItemTypeId(@PathVariable id: Long): ResponseEntity<Iterable<ItemResource>> {
        logger.info("=> GET request for all items with itemTypeId $id")

        val selfLink = linkTo<ItemController> { handleGetItemsByItemTypeId(id) }
            .withSelfRel()
            .withMethods(HttpMethod.GET)

        val itemResources = this.itemService.getItemsForItemType(id).map { ItemResource.from(it) }

        val collectionModel = CollectionModel.of(itemResources, selfLink)

        return ResponseEntity(collectionModel, HttpStatus.OK)
    }
}
