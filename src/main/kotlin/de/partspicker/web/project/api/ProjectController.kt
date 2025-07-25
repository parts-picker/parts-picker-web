package de.partspicker.web.project.api

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.project.api.requests.ProjectCopyRequest
import de.partspicker.web.project.api.requests.ProjectDescriptionPatchRequest
import de.partspicker.web.project.api.requests.ProjectMetaInfoPatchRequest
import de.partspicker.web.project.api.requests.ProjectNamePatchRequest
import de.partspicker.web.project.api.requests.ProjectPatchRequest
import de.partspicker.web.project.api.requests.ProjectPostRequest
import de.partspicker.web.project.api.resources.ProjectResource
import de.partspicker.web.project.api.resources.ProjectResourceAssembler
import de.partspicker.web.project.business.ProjectService
import de.partspicker.web.project.business.objects.CreateProject
import de.partspicker.web.project.business.objects.Project
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
class ProjectController(
    private val projectService: ProjectService,
    private val pagedResourcesAssembler: PagedResourcesAssembler<Project>,
    private val projectResourceAssembler: ProjectResourceAssembler
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @PostMapping("/projects")
    fun handlePostProject(@RequestBody body: ProjectPostRequest): ResponseEntity<ProjectResource> {
        logger.info("=> POST request for new project")

        val createdProject = this.projectService.create(CreateProject.from(body))

        return ResponseEntity(projectResourceAssembler.toModel(createdProject), HttpStatus.OK)
    }

    @PostMapping("/projects/{id}/copies")
    fun handleCopyProject(
        @PathVariable id: Long,
        @Valid @RequestBody projectCopyRequest: ProjectCopyRequest
    ): ResponseEntity<ProjectResource> {
        logger.info("=> POST request for creating a copy with name '${projectCopyRequest.name}' of project with id $id")

        val copiedProject = this.projectService.copy(name = projectCopyRequest.name, sourceProjectId = id)

        return ResponseEntity(projectResourceAssembler.toModel(copiedProject), HttpStatus.OK)
    }

    @GetMapping("/projects")
    fun handleGetAllProjects(pageable: Pageable): ResponseEntity<PagedModel<ProjectResource>> {
        logger.info("=> GET request for all projects")

        val projects = this.projectService.readAll(pageable)
        val pagedResource = this.pagedResourcesAssembler.toModel(projects, projectResourceAssembler)

        return ResponseEntity(pagedResource, HttpStatus.OK)
    }

    @GetMapping("/projects/{id}")
    fun handleGetProjectById(@PathVariable id: Long): ResponseEntity<ProjectResource> {
        logger.info("=> GET request for project with id $id")

        val projectResource = projectResourceAssembler.toModel(this.projectService.read(id))

        return ResponseEntity(projectResource, HttpStatus.OK)
    }

    @PatchMapping("/projects/{id}")
    fun handlePatchProject(
        @PathVariable id: Long,
        @Valid @RequestBody body: ProjectPatchRequest
    ): ResponseEntity<ProjectResource> {
        logger.info("=> PATCH request to modify project with $id")

        val updatedProject = when (body) {
            is ProjectDescriptionPatchRequest -> this.projectService.updateDescription(
                projectId = id,
                description = body.description
            )

            is ProjectMetaInfoPatchRequest -> this.projectService.update(
                projectId = id,
                shortDescription = body.shortDescription,
                groupId = body.groupId
            )

            is ProjectNamePatchRequest -> this.projectService.updateName(
                projectId = id,
                name = body.name
            )
        }

        val projectResource = this.projectResourceAssembler.toModel(updatedProject)

        return ResponseEntity(projectResource, HttpStatus.OK)
    }

    @DeleteMapping("/projects/{id}")
    fun handleDeleteProject(@PathVariable id: Long): ResponseEntity<Unit> {
        logger.info("=> DELETE request for project with id $id")

        this.projectService.delete(id)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
