package de.partspicker.web.project.api

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.project.api.requests.PostProjectRequest
import de.partspicker.web.project.api.requests.PutProjectRequest
import de.partspicker.web.project.api.requests.asEntity
import de.partspicker.web.project.api.responses.ProjectIterableResponse
import de.partspicker.web.project.api.responses.ProjectResponse
import de.partspicker.web.project.api.responses.asResponse
import de.partspicker.web.project.business.ProjectService
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
class ProjectController(private val projectService: ProjectService) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @GetMapping("/project")
    fun handleGetAllProjects(): ResponseEntity<ProjectIterableResponse> {
        logger.info("=> GET request for all projects")

        return ResponseEntity(this.projectService.findAll().asResponse(), HttpStatus.OK)
    }

    @GetMapping("/project/{id}")
    fun handleGetProjectById(@PathVariable id: Long): ResponseEntity<ProjectResponse> {
        logger.info("=> GET request for project with id $id")

        return ResponseEntity(projectService.findById(id).asResponse(), HttpStatus.OK)
    }

    @PostMapping("/project")
    fun handleAddProject(@RequestBody body: PostProjectRequest): ResponseEntity<ProjectResponse> {
        logger.info("=> POST request for new project")

        return ResponseEntity(projectService.save(body.asEntity()).asResponse(), HttpStatus.OK)
    }

    @PutMapping("/project/{id}")
    fun handleModifyProject(
        @PathVariable id: Long,
        @RequestBody body: PutProjectRequest
    ): ResponseEntity<ProjectResponse> {
        logger.info("=> PUT request to modify project with $id")

        val project = this.projectService.update(body.asEntity(id))

        return ResponseEntity(project.asResponse(), HttpStatus.OK)
    }

    @DeleteMapping("/project/{id}")
    fun handleDeleteProject(@PathVariable id: Long): ResponseEntity<Unit> {
        logger.info("=> DELETE request for project with id $id")

        this.projectService.deleteById(id)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
