package de.partspicker.web.orgunit.api.resources

import de.partspicker.web.common.hal.generateGetAllGroupsLink
import de.partspicker.web.common.hal.generateGetAllItemTypesLink
import de.partspicker.web.common.hal.generateGetAllItemsLink
import de.partspicker.web.common.hal.generateGetAllProjectsLink
import de.partspicker.web.common.hal.generateGetOrgUnitByIdLink
import de.partspicker.web.item.api.resources.ItemResource
import de.partspicker.web.item.api.resources.ItemTypeResource
import de.partspicker.web.orgunit.api.responses.AccessLevelResponse
import de.partspicker.web.orgunit.business.objects.OrgUnitMembership
import de.partspicker.web.project.api.resources.GroupResource
import de.partspicker.web.project.api.resources.ProjectResource
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

@Component
class OrgUnitMembershipResourceAssembler :
    RepresentationModelAssembler<OrgUnitMembership, OrgUnitMembershipResource> {
    override fun toModel(orgUnitMembership: OrgUnitMembership): OrgUnitMembershipResource {
        return OrgUnitMembershipResource(
            orgUnit = OrgUnitSummaryResource.from(orgUnitMembership.orgUnit),
            accessLevel = AccessLevelResponse.from(orgUnitMembership.accessLevel),
            joinedOn = orgUnitMembership.joinedOn,
            links = generateDefaultLinks(orgUnitMembership.orgUnit.id)
        )
    }

    private fun generateDefaultLinks(orgUnitId: Long): List<Link> {
        return listOf(
            generateGetOrgUnitByIdLink(IanaLinkRelations.SELF, orgUnitId),
            generateGetAllProjectsLink(ProjectResource.COLLECTION_RELATION_NAME, orgUnitId),
            generateGetAllItemTypesLink(ItemTypeResource.COLLECTION_RELATION_NAME, orgUnitId),
            generateGetAllItemsLink(ItemResource.COLLECTION_RELATION_NAME, orgUnitId),
            generateGetAllGroupsLink(GroupResource.COLLECTION_RELATION_NAME, orgUnitId)
        )
    }
}
