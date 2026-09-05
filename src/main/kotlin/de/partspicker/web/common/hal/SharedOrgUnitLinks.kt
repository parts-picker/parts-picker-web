package de.partspicker.web.common.hal // ktlint-disable filename

import de.partspicker.web.orgunit.api.OrgUnitController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.server.mvc.linkTo

fun generateGetAllOrgUnitsOfCurrentUserLink(relation: LinkRelation) =
    generateGetAllOrgUnitsOfCurrentUserLink(relation.toString())

fun generateGetAllOrgUnitsOfCurrentUserLink(relation: String): Link {
    val uriTemplate = UriTemplate.of(
        linkTo<OrgUnitController> { handleGetAllOrgUnitsOfCurrentUser(Pageable.unpaged()) }.toUri().toString()
    ).withPaginationParams()

    return Link.of(uriTemplate, relation).withName(DefaultName.READ)
}
