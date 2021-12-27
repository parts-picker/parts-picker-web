package de.partspicker.web.item.api

import de.partspicker.web.common.hal.withMethods
import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.item.api.requests.ItemTypePostRequest
import de.partspicker.web.item.api.resources.ItemTypeResource
import de.partspicker.web.item.business.ItemTypeService
import de.partspicker.web.item.business.objects.ItemType
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class ItemTypeController(
    private val itemTypeService: ItemTypeService
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @PostMapping("/item-types")
    fun handlePostItemType(@RequestBody body: ItemTypePostRequest): ResponseEntity<ItemTypeResource> {
        logger.info("=> POST request for new item type")

        val createdItemType = this.itemTypeService.create(ItemType.from(body))

        return ResponseEntity(ItemTypeResource.from(createdItemType), HttpStatus.OK)
    }

    @GetMapping("/item-types")
    fun handleGetAllItemTypes(): ResponseEntity<Iterable<ItemTypeResource>> {
        logger.info("=> GET request for all item types")

        val itemTypeResource = ItemTypeResource.AsList.from(this.itemTypeService.getItemTypes())

        val selfLink = linkTo<ItemTypeController> { handleGetAllItemTypes() }.withSelfRel().withMethods(HttpMethod.GET)
        val collectionModel = CollectionModel.of(itemTypeResource, selfLink)

        return ResponseEntity(collectionModel, HttpStatus.OK)
    }

    @GetMapping("/item-types/{id}")
    fun handleGetItemTypeById(@PathVariable id: Long): ResponseEntity<ItemTypeResource> {
        logger.info("=> GET request for item type with id $id")

        val itemTypeResource = ItemTypeResource.from(this.itemTypeService.getItemTypeById(id))

        return ResponseEntity(itemTypeResource, HttpStatus.OK)
    }

    @DeleteMapping("/item-types/{id}")
    fun handleDeleteItemTypeById(@PathVariable id: Long): ResponseEntity<Unit> {
        logger.info("=> DELETE request for item type with id $id")

        this.itemTypeService.deleteItemTypeById(id)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
