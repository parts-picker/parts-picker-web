package de.parts_picker.web.project.api

import de.parts_picker.web.common.util.Logging
import de.parts_picker.web.common.util.logger
import de.parts_picker.web.project.api.requests.GroupRequest
import de.parts_picker.web.project.api.requests.PutGroupRequest
import de.parts_picker.web.project.api.requests.asEntity
import de.parts_picker.web.project.api.responses.GroupIterableResponse
import de.parts_picker.web.project.api.responses.GroupResponse
import de.parts_picker.web.project.api.responses.asResponse
import de.parts_picker.web.project.business.GroupService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class GroupController (private val groupService: GroupService) {
    companion object : Logging {
        val logger = logger()
    }

    @GetMapping("/group")
    fun handleGetAllGroups(): ResponseEntity<GroupIterableResponse> {
        logger.info("GET request for all groups")

        return ResponseEntity(this.groupService.findAll().asResponse(), HttpStatus.OK)
    }

    @GetMapping("/group/{id}")
    fun handleGetGroupById(@PathVariable id: Long): ResponseEntity<GroupResponse> {
        logger.info("=> GET request for group with id $id")

        return ResponseEntity(this.groupService.findById(id).asResponse(), HttpStatus.OK)
    }

    @PostMapping("/group")
    fun handleAddGroup(@RequestBody body: GroupRequest): ResponseEntity<GroupResponse> {
        logger.info("=> POST request for new group")

        val group = this.groupService.save(body.asEntity())

        return ResponseEntity(group.asResponse(), HttpStatus.CREATED)
    }

    @PutMapping("/group/{id}")
    fun handleModifyGroup(@PathVariable id: Long, @RequestBody body: PutGroupRequest): ResponseEntity<GroupResponse> {
        logger.info("=> PUT request to modify group with id $id")

        val group = this.groupService.update(body.asEntity(id))

        return ResponseEntity(group.asResponse(), HttpStatus.OK)
    }

    @DeleteMapping("/group/{id}")
    fun handleDeleteGroup(@PathVariable id: Long): ResponseEntity<Unit> {
        logger.info("=> DELETE request for group with id $id")

        this.groupService.deleteById(id)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}