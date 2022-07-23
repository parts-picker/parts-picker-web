package de.partspicker.web.item.api

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.item.api.requests.ItemTypePostRequest
import de.partspicker.web.item.api.requests.ItemTypePutRequest
import de.partspicker.web.item.api.resources.ItemTypeResource
import de.partspicker.web.item.api.resources.ItemTypeResourceAssembler
import de.partspicker.web.item.business.ItemTypeService
import de.partspicker.web.item.business.objects.ItemType
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class ItemTypeController(
    private val itemTypeService: ItemTypeService,
    private val pagedResourcesAssembler: PagedResourcesAssembler<ItemType>,
    private val itemTypeResourceAssembler: ItemTypeResourceAssembler
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @PostMapping("/item-types")
    fun handlePostItemType(@RequestBody body: ItemTypePostRequest): ResponseEntity<ItemTypeResource> {
        logger.info("=> POST request for new item type")

        val createdItemType = this.itemTypeService.create(ItemType.from(body))

        return ResponseEntity(itemTypeResourceAssembler.toModel(createdItemType), HttpStatus.OK)
    }

    @GetMapping("/item-types")
    fun handleGetAllItemTypes(pageable: Pageable): ResponseEntity<PagedModel<ItemTypeResource>> {
        logger.info("=> GET request for all item types")

        val itemTypes = this.itemTypeService.getItemTypes(pageable)
        val pagedModel = pagedResourcesAssembler.toModel(itemTypes, itemTypeResourceAssembler)

        return ResponseEntity(pagedModel, HttpStatus.OK)
    }

    @GetMapping("/item-types/{id}")
    fun handleGetItemTypeById(@PathVariable id: Long): ResponseEntity<ItemTypeResource> {
        logger.info("=> GET request for item type with id $id")

        val itemTypeResource = itemTypeResourceAssembler.toModel(this.itemTypeService.getItemTypeById(id))

        return ResponseEntity(itemTypeResource, HttpStatus.OK)
    }

    @PutMapping("/item-types/{id}")
    fun handlePutItemTypeById(
        @PathVariable id: Long,
        @RequestBody body: ItemTypePutRequest
    ): ResponseEntity<ItemTypeResource> {
        logger.info("=> PUT request for item type with id $id")

        val itemType = ItemType.from(body, id)
        val updatedResource = itemTypeResourceAssembler.toModel(this.itemTypeService.update(itemType))

        return ResponseEntity(updatedResource, HttpStatus.OK)
    }

    @DeleteMapping("/item-types/{id}")
    fun handleDeleteItemTypeById(@PathVariable id: Long): ResponseEntity<Unit> {
        logger.info("=> DELETE request for item type with id $id")

        this.itemTypeService.deleteItemTypeById(id)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
