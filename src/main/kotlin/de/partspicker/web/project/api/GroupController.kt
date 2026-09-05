package de.partspicker.web.project.api

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.project.api.requests.GroupPostRequest
import de.partspicker.web.project.api.requests.GroupPutRequest
import de.partspicker.web.project.api.resources.GroupResource
import de.partspicker.web.project.api.resources.GroupResourceAssembler
import de.partspicker.web.project.business.GroupService
import de.partspicker.web.project.business.objects.Group
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
class GroupController(
    private val groupService: GroupService,
    private val groupResourceAssembler: GroupResourceAssembler,
    private val pagedResourcesAssembler: PagedResourcesAssembler<Group>
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @GetMapping("/org-units/{orgUnitId}/groups")
    fun handleGetAllGroups(
        @PathVariable orgUnitId: Long,
        pageable: Pageable
    ): ResponseEntity<PagedModel<GroupResource>> {
        logger.info("=> GET request for all groups of org unit with id $orgUnitId")

        val groups = this.groupService.findAllForOrgUnit(orgUnitId, pageable)
        val pagedResource = this.pagedResourcesAssembler.toModel(groups, groupResourceAssembler)

        return ResponseEntity(pagedResource, HttpStatus.OK)
    }

    @GetMapping("/groups/{id}")
    fun handleGetGroupById(@PathVariable id: Long): ResponseEntity<GroupResource> {
        logger.info("=> GET request for group with id $id")

        return ResponseEntity(groupResourceAssembler.toModel(this.groupService.getById(id)), HttpStatus.OK)
    }

    @PostMapping("/org-units/{orgUnitId}/groups")
    fun handlePostGroup(
        @PathVariable orgUnitId: Long,
        @RequestBody body: GroupPostRequest
    ): ResponseEntity<GroupResource> {
        logger.info("=> POST request for new group in org unit with id $orgUnitId")

        val group = this.groupService.create(orgUnitId, body.name, body.description)

        return ResponseEntity(groupResourceAssembler.toModel(group), HttpStatus.CREATED)
    }

    @PutMapping("/groups/{id}")
    fun handlePutGroup(@PathVariable id: Long, @RequestBody body: GroupPutRequest): ResponseEntity<GroupResource> {
        logger.info("=> PUT request to modify group with id $id")

        val group = this.groupService.update(id, body.name, body.description)

        return ResponseEntity(groupResourceAssembler.toModel(group), HttpStatus.OK)
    }

    @DeleteMapping("/groups/{id}")
    fun handleDeleteGroup(@PathVariable id: Long): ResponseEntity<Unit> {
        logger.info("=> DELETE request for group with id $id")

        this.groupService.delete(id)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
