package de.partspicker.web.project.api.resources

import de.partspicker.web.common.business.objects.enums.AccessLevel.CONFIGURE
import de.partspicker.web.common.business.objects.enums.AccessLevel.MAINTAIN
import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.DELETE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.LinkListBuilder
import de.partspicker.web.common.hal.generateGetAllGroupsLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.project.api.GroupController
import de.partspicker.web.project.api.requests.GroupPostRequest
import de.partspicker.web.project.api.requests.GroupPutRequest
import de.partspicker.web.project.business.objects.Group
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class GroupResourceAssembler(
    private val orgUnitAccessService: OrgUnitAccessService
) : RepresentationModelAssembler<Group, GroupResource> {
    override fun toModel(group: Group): GroupResource {
        return GroupResource(
            id = group.id,
            name = group.name!!,
            description = group.description,
            links = generateDefaultLinks(group)
        )
    }

    private fun generateDefaultLinks(group: Group): List<Link> {
        return LinkListBuilder()
            .with(
                linkTo<GroupController> { handlePostGroup(group.orgUnitId, GroupPostRequest.DUMMY) }
                    .withRel(IanaLinkRelations.COLLECTION)
                    .withName(CREATE),
                this.orgUnitAccessService.atLeast(group.orgUnitId, CONFIGURE)
            )
            .with(
                linkTo<GroupController> { handleGetGroupById(group.id) }
                    .withSelfRel()
                    .withName(READ)
            )
            .with(generateGetAllGroupsLink(IanaLinkRelations.COLLECTION, group.orgUnitId))
            .with(
                linkTo<GroupController> { handlePutGroup(group.id, GroupPutRequest.DUMMY) }
                    .withSelfRel()
                    .withName(UPDATE),
                this.orgUnitAccessService.atLeast(group.orgUnitId, CONFIGURE)
            )
            .with(
                linkTo<GroupController> { handleDeleteGroup(group.id) }
                    .withSelfRel()
                    .withName(DELETE),
                this.orgUnitAccessService.memberCreatorOrAtLeast(group.orgUnitId, group.createdById, MAINTAIN)
            )
            .build()
    }
}
