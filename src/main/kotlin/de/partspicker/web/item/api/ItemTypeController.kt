package de.partspicker.web.item.api

import de.partspicker.web.common.hal.withMethods
import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.item.api.resources.ItemTypeResource
import de.partspicker.web.item.business.ItemTypeService
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ItemTypeController(
    private val itemTypeService: ItemTypeService
) {
    companion object : LoggingUtil {
        val logger = logger()
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
        logger.info("=> GET request for item with id $id")

        val itemTypeResource = ItemTypeResource.from(this.itemTypeService.getItemTypeById(id))

        return ResponseEntity(itemTypeResource, HttpStatus.OK)
    }
}
