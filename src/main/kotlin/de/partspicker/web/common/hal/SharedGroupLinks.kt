package de.partspicker.web.common.hal // ktlint-disable filename

import de.partspicker.web.project.api.GroupController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.server.mvc.linkTo

fun generateGetAllGroupsLink(relation: LinkRelation, orgUnitId: Long) =
    generateGetAllGroupsLink(relation.toString(), orgUnitId)

fun generateGetAllGroupsLink(relation: String, orgUnitId: Long): Link {
    val uriTemplate = UriTemplate.of(
        linkTo<GroupController> { handleGetAllGroups(orgUnitId, Pageable.unpaged()) }.toUri().toString()
    ).withPaginationParams()

    return Link.of(uriTemplate, relation).withName(DefaultName.READ)
}
