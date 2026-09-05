package de.partspicker.web.orgunit.api.resources

import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.generateGetAllGroupsLink
import de.partspicker.web.common.hal.generateGetAllItemTypesLink
import de.partspicker.web.common.hal.generateGetAllItemsLink
import de.partspicker.web.common.hal.generateGetAllOrgUnitsOfCurrentUserLink
import de.partspicker.web.common.hal.generateGetAllProjectsLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.item.api.resources.ItemResource
import de.partspicker.web.item.api.resources.ItemTypeResource
import de.partspicker.web.orgunit.api.OrgUnitController
import de.partspicker.web.orgunit.api.requests.OrgUnitPostRequest
import de.partspicker.web.orgunit.business.objects.OrgUnit
import de.partspicker.web.project.api.resources.GroupResource
import de.partspicker.web.project.api.resources.ProjectResource
import de.partspicker.web.user.api.resources.UserResource
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class OrgUnitResourceAssembler : RepresentationModelAssembler<OrgUnit, OrgUnitResource> {
    override fun toModel(orgUnit: OrgUnit): OrgUnitResource {
        return OrgUnitResource(
            name = orgUnit.name,
            shortDescription = orgUnit.shortDescription,
            owner = UserResource.from(orgUnit.owner),
            createdBy = UserResource.from(orgUnit.createdBy),
            createdOn = orgUnit.createdOn,
            links = generateDefaultLinks(orgUnit.id)
        )
    }

    private fun generateDefaultLinks(orgUnitId: Long): List<Link> {
        return listOf(
            linkTo<OrgUnitController> { handleGetOrgUnitById(orgUnitId) }
                .withSelfRel()
                .withName(READ),
            generateGetAllOrgUnitsOfCurrentUserLink(OrgUnitResource.COLLECTION_RELATION_NAME),
            linkTo<OrgUnitController> { handlePostOrgUnit(OrgUnitPostRequest.DUMMY) }
                .withRel(OrgUnitResource.COLLECTION_RELATION_NAME)
                .withName(CREATE),
            generateGetAllProjectsLink(ProjectResource.COLLECTION_RELATION_NAME, orgUnitId),
            generateGetAllItemTypesLink(ItemTypeResource.COLLECTION_RELATION_NAME, orgUnitId),
            generateGetAllItemsLink(ItemResource.COLLECTION_RELATION_NAME, orgUnitId),
            generateGetAllGroupsLink(GroupResource.COLLECTION_RELATION_NAME, orgUnitId)
        )
    }
}
