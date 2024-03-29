package de.partspicker.web.inventory.api

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.inventory.api.requests.RequiredItemTypePatchRequest
import de.partspicker.web.inventory.api.requests.RequiredItemTypePostRequest
import de.partspicker.web.inventory.api.resources.RequiredItemTypeResource
import de.partspicker.web.inventory.api.resources.RequiredItemTypeResourceAssembler
import de.partspicker.web.inventory.business.RequiredItemTypeReadService
import de.partspicker.web.inventory.business.RequiredItemTypeService
import de.partspicker.web.inventory.business.objects.CreateOrUpdateRequiredItemType
import de.partspicker.web.inventory.business.objects.RequiredItemType
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RequiredItemTypeController(
    private val requiredItemTypeReadService: RequiredItemTypeReadService,
    private val requiredItemTypeService: RequiredItemTypeService,
    private val pagedResourcesAssembler: PagedResourcesAssembler<RequiredItemType>,
    private val requiredItemTypeResourceAssembler: RequiredItemTypeResourceAssembler
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @PostMapping("/projects/{projectId}/required/{itemTypeId}")
    fun handlePostRequiredItemTypes(
        @PathVariable projectId: Long,
        @PathVariable itemTypeId: Long,
        @Valid @RequestBody
        body: RequiredItemTypePostRequest
    ): ResponseEntity<RequiredItemTypeResource> {
        logger.info("=> POST request for a new required item type")

        val createdRequiredItemType = this.requiredItemTypeService.createOrUpdate(
            CreateOrUpdateRequiredItemType.from(
                body,
                projectId,
                itemTypeId
            )
        )

        return ResponseEntity(requiredItemTypeResourceAssembler.toModel(createdRequiredItemType), HttpStatus.OK)
    }

    @GetMapping("/projects/{projectId}/required")
    fun handleGetAllByProjectId(
        @PathVariable projectId: Long,
        pageable: Pageable
    ): ResponseEntity<PagedModel<RequiredItemTypeResource>> {
        logger.info("=> GET request for all required item types for project with id $projectId")

        val requiredItemTypes = this.requiredItemTypeReadService.readAllByProjectId(projectId, pageable)
        val pagedResource = this.pagedResourcesAssembler.toModel(requiredItemTypes, requiredItemTypeResourceAssembler)

        return ResponseEntity(pagedResource, HttpStatus.OK)
    }

    @PatchMapping("/projects/{projectId}/required/{itemTypeId}")
    fun handlePatchByProjectIdAndItemTypeId(
        @PathVariable projectId: Long,
        @PathVariable itemTypeId: Long,
        @Valid @RequestBody
        body: RequiredItemTypePatchRequest
    ): ResponseEntity<RequiredItemTypeResource> {
        logger.info(
            "=> PATCH request to change requiredAmount for" +
                "requiredItemType with project id $projectId & itemTypeId id $itemTypeId"
        )

        val updatedRequiredItemType = this.requiredItemTypeService.createOrUpdate(
            CreateOrUpdateRequiredItemType.from(body, projectId, itemTypeId)
        )

        return ResponseEntity(requiredItemTypeResourceAssembler.toModel(updatedRequiredItemType), HttpStatus.OK)
    }

    @DeleteMapping("/projects/{projectId}/required/{itemTypeId}")
    fun handleDeleteByProjectIdAndItemTypeId(
        @PathVariable projectId: Long,
        @PathVariable itemTypeId: Long
    ): ResponseEntity<Unit> {
        logger.info("=> DELETE request for requiredItemType with project id $projectId & itemTypeId id $itemTypeId")

        this.requiredItemTypeService.delete(projectId, itemTypeId)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
