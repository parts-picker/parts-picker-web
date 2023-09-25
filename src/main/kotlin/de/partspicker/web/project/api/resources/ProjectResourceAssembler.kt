package de.partspicker.web.project.api.resources

import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.common.business.rules.or
import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.DELETE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.LinkListBuilder
import de.partspicker.web.common.hal.RelationName
import de.partspicker.web.common.hal.RelationName.ASSIGNED
import de.partspicker.web.common.hal.RelationName.AVAILABLE
import de.partspicker.web.common.hal.RelationName.COPIED_FROM
import de.partspicker.web.common.hal.generateGetAllProjectsLink
import de.partspicker.web.common.hal.generateGetAllRequiredItemTypesLink
import de.partspicker.web.common.hal.generateSearchItemsByNameLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.project.api.ProjectController
import de.partspicker.web.project.api.requests.ProjectCopyRequest
import de.partspicker.web.project.api.requests.ProjectMetaInfoPatchRequest
import de.partspicker.web.project.api.requests.ProjectPostRequest
import de.partspicker.web.project.business.objects.Project
import de.partspicker.web.project.business.rules.ProjectActiveRule
import de.partspicker.web.workflow.api.WorkflowInteractionController
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
            status = project.status,
            displayStatus = project.displayStatus,
            shortDescription = project.shortDescription,
            description = project.description,
            groupId = project.group?.id,
            links = generateDefaultLinks(project)
        )
    }

    private fun generateDefaultLinks(project: Project): List<Link> {
        return LinkListBuilder()
            .with(
                linkTo<ProjectController> { handlePostProject(ProjectPostRequest.DUMMY) }
                    .withRel(IanaLinkRelations.COLLECTION)
                    .withName(CREATE)
            )
            .with(generateGetAllProjectsLink(IanaLinkRelations.COLLECTION))
            .with(
                linkTo<ProjectController> { handleGetProjectById(project.id) }
                    .withSelfRel()
                    .withName(READ)
            )
            .with(
                linkTo<ProjectController> { handlePatchProject(project.id, ProjectMetaInfoPatchRequest.DUMMY) }
                    .withSelfRel()
                    .withName(UPDATE),
                ProjectActiveRule(project)
            )
            .with(
                linkTo<ProjectController> { handleDeleteProject(project.id) }
                    .withSelfRel()
                    .withName(DELETE),
                NodeNameEqualsRule(project.status, "planning") or
                    NodeNameEqualsRule(project.status, "implementation")

            )
            // source project
            .withCondition(
                linkTo<ProjectController> { handleGetProjectById(project.sourceProjectId ?: 0L) }
                    .withRel(COPIED_FROM)
                    .withName(READ),
                project.sourceProjectId != null
            )
            // copies
            .with(
                linkTo<ProjectController> { handleCopyProject(project.id, ProjectCopyRequest.DUMMY) }
                    .withRel(RelationName.COPIES)
                    .withName(CREATE)
            )
            // availableItemType
            .with(
                generateSearchItemsByNameLink(AVAILABLE, project.id),
                NodeNameEqualsRule(project.status, "planning"),
                ProjectActiveRule(project)
            )
            // requiredItemType
            .with(
                generateGetAllRequiredItemTypesLink(ASSIGNED, project.id)
            )
            // workflow
            .with(
                linkTo<WorkflowInteractionController> {
                    handleGetInstanceInfo(project.workflowInstanceId)
                }
                    .withRel(RelationName.STATUS)
                    .withName(READ)
            )
            .build()
    }
}
