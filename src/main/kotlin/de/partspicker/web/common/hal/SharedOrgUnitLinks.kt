package de.partspicker.web.common.hal

import de.partspicker.web.orgunit.api.OrgUnitController
import de.partspicker.web.orgunit.api.requests.OrgUnitPostRequest
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

fun generatePostOrgUnitLink(relation: LinkRelation) = generatePostOrgUnitLink(relation.toString())

fun generatePostOrgUnitLink(relation: String): Link =
    linkTo<OrgUnitController> { handlePostOrgUnit(OrgUnitPostRequest.DUMMY) }
        .withRel(relation)
        .withName(DefaultName.CREATE)

fun generateGetOrgUnitByIdLink(relation: LinkRelation, orgUnitId: Long) =
    generateGetOrgUnitByIdLink(relation.toString(), orgUnitId)

fun generateGetOrgUnitByIdLink(relation: String, orgUnitId: Long): Link =
    linkTo<OrgUnitController> { handleGetOrgUnitById(orgUnitId) }
        .withRel(relation)
        .withName(DefaultName.READ)
