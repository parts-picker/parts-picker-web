package de.partspicker.web.inventory.api

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.inventory.api.requests.RequiredItemTypePostRequest
import de.partspicker.web.inventory.api.resources.RequiredItemTypeResource
import de.partspicker.web.inventory.api.resources.RequiredItemTypeResourceAssembler
import de.partspicker.web.inventory.business.RequiredItemTypeService
import de.partspicker.web.inventory.business.objects.RequiredItemType
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class RequiredItemTypeController(
    private val requiredItemTypesService: RequiredItemTypeService,
    private val pagedResourcesAssembler: PagedResourcesAssembler<RequiredItemType>,
    private val requiredItemTypeResourceAssembler: RequiredItemTypeResourceAssembler
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @PostMapping("/projects/{projectId}/required")
    fun handlePostRequiredItemTypes(
        @PathVariable projectId: Long,
        @Valid @RequestBody
        body: RequiredItemTypePostRequest
    ): ResponseEntity<RequiredItemTypeResource> {
        logger.info("=> POST request for a new required item type")

        val createdRequiredItemType = this.requiredItemTypesService.create(RequiredItemType.from(body, projectId))

        return ResponseEntity(requiredItemTypeResourceAssembler.toModel(createdRequiredItemType), HttpStatus.OK)
    }

    @GetMapping("/projects/{projectId}/required")
    fun handleGetAllByProjectId(
        @PathVariable projectId: Long,
        pageable: Pageable
    ): ResponseEntity<PagedModel<RequiredItemTypeResource>> {
        logger.info("=> GET request for all required item types for project with id $projectId")

        val requiredItemTypes = this.requiredItemTypesService.readAllByProjectId(projectId, pageable)
        val pagedResource = this.pagedResourcesAssembler.toModel(requiredItemTypes, requiredItemTypeResourceAssembler)

        return ResponseEntity(pagedResource, HttpStatus.OK)
    }
}
