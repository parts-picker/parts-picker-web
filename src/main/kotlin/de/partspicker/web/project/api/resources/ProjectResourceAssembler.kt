package de.partspicker.web.project.api.resources

import de.partspicker.web.common.hal.DefaultName
import de.partspicker.web.common.hal.generateGetAllProjectsLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.project.api.ProjectController
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
            groupId = project.groupId,
            links = generateDefaultLinks(project.id)
        )
    }

    private fun generateDefaultLinks(projectId: Long): List<Link> {
        return listOf(
            generateGetAllProjectsLink(IanaLinkRelations.COLLECTION),
            linkTo<ProjectController> { handleGetProjectById(projectId) }
                .withSelfRel()
                .withName(DefaultName.READ)
        )
    }
}
