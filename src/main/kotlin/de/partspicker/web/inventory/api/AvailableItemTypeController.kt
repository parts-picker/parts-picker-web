
package de.partspicker.web.inventory.api

import de.partspicker.web.common.hal.DefaultName.SEARCH
import de.partspicker.web.common.hal.toCollectionModel
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.inventory.api.resources.AvailableItemTypeResource
import de.partspicker.web.inventory.api.resources.AvailableItemTypeResourceAssembler
import de.partspicker.web.inventory.business.AvailableItemTypeService
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AvailableItemTypeController(
    private val availableItemTypeService: AvailableItemTypeService,
    private val availableItemTypeResourceAssembler: AvailableItemTypeResourceAssembler
) {

    companion object : LoggingUtil {
        val logger = logger()
    }

    @GetMapping("/projects/{projectId}/available")
    fun handleSearch(
        @PathVariable projectId: Long,
        @RequestParam name: String
    ): ResponseEntity<CollectionModel<AvailableItemTypeResource>> {
        logger.info("=> GET request for searching for an item type with name '$name'")

        val availableItemTypes = this.availableItemTypeService.searchByName(name, projectId)

        val selfLink = linkTo<AvailableItemTypeController> { handleSearch(projectId, name) }
            .withSelfRel()
            .withName(SEARCH)
        val collectionModel = availableItemTypeResourceAssembler.toCollectionModel(availableItemTypes, selfLink)

        return ResponseEntity(collectionModel, HttpStatus.OK)
    }
}
