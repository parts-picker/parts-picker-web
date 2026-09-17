package de.partspicker.web.common.hal

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.inventory.api.RequiredItemTypeController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.server.mvc.linkTo

fun generateGetAllRequiredItemTypesLink(relation: RelationName, projectId: Long) =
    generateGetAllRequiredItemTypesLink(relation.displayName, projectId)

fun generateGetAllRequiredItemTypesLink(relation: LinkRelation, projectId: Long) =
    generateGetAllRequiredItemTypesLink(relation.toString(), projectId)

fun generateGetAllRequiredItemTypesLink(relation: String, projectId: Long): Link {
    val uriTemplate = UriTemplate.of(
        linkTo<RequiredItemTypeController> { handleGetAllByProjectId(projectId, Pageable.unpaged()) }.toUri().toString()
    ).withPaginationParams()

    return Link.of(uriTemplate, relation).withName(READ)
}
