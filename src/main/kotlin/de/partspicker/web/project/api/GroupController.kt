package de.partspicker.web.project.api

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.project.api.requests.PostGroupRequest
import de.partspicker.web.project.api.requests.PutGroupRequest
import de.partspicker.web.project.api.requests.asEntity
import de.partspicker.web.project.api.responses.GroupIterableResponse
import de.partspicker.web.project.api.responses.GroupResponse
import de.partspicker.web.project.api.responses.asResponse
import de.partspicker.web.project.business.GroupService
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
class GroupController(private val groupService: GroupService) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @GetMapping("/group")
    fun handleGetAllGroups(): ResponseEntity<GroupIterableResponse> {
        logger.info("=> GET request for all groups")

        return ResponseEntity(this.groupService.findAll().asResponse(), HttpStatus.OK)
    }

    @GetMapping("/group/{id}")
    fun handleGetGroupById(@PathVariable id: Long): ResponseEntity<GroupResponse> {
        logger.info("=> GET request for group with id $id")

        return ResponseEntity(this.groupService.findById(id).asResponse(), HttpStatus.OK)
    }

    @PostMapping("/group")
    fun handleAddGroup(@RequestBody body: PostGroupRequest): ResponseEntity<GroupResponse> {
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
