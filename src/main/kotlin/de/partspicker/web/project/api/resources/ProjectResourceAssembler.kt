package de.partspicker.web.project.api.resources

import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.DELETE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.generateGetAllProjectsLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.project.api.ProjectController
import de.partspicker.web.project.api.requests.ProjectPostRequest
import de.partspicker.web.project.api.requests.ProjectPutRequest
import de.partspicker.web.project.business.objects.Project
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class ProjectResourceAssembler : RepresentationModelAssembler<Project, ProjectResource> {
    override fun toModel(project: Project): ProjectResource {
        return ProjectResource(
            id = project.id,
            name = project.name,
            description = project.description,
            groupId = project.group?.id,
            links = generateDefaultLinks(project.id)
        )
    }

    private fun generateDefaultLinks(projectId: Long): List<Link> {
        return listOf(
            linkTo<ProjectController> { handlePostProject(ProjectPostRequest.DUMMY) }
                .withRel(IanaLinkRelations.COLLECTION)
                .withName(CREATE),
            generateGetAllProjectsLink(IanaLinkRelations.COLLECTION),
            linkTo<ProjectController> { handleGetProjectById(projectId) }
                .withSelfRel()
                .withName(READ),
            linkTo<ProjectController> { handlePutProject(projectId, ProjectPutRequest.DUMMY) }
                .withSelfRel()
                .withName(UPDATE),
            linkTo<ProjectController> { handleDeleteProject(projectId) }
                .withSelfRel()
                .withName(DELETE)
        )
    }
}
