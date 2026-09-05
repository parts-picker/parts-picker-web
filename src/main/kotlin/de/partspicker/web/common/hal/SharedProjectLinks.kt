package de.partspicker.web.common.hal // ktlint-disable filename

import de.partspicker.web.project.api.ProjectController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.server.mvc.linkTo

fun generateGetAllProjectsLink(relation: LinkRelation, orgUnitId: Long) =
    generateGetAllProjectsLink(relation.toString(), orgUnitId)

fun generateGetAllProjectsLink(relation: String, orgUnitId: Long): Link {
    // READ projects link with page meta information params
    val uriTemplate = UriTemplate.of(
        linkTo<ProjectController> { handleGetAllProjects(orgUnitId, Pageable.unpaged()) }.toUri().toString()
    ).withPaginationParams()

    return Link.of(uriTemplate, relation).withName(DefaultName.READ)
}
