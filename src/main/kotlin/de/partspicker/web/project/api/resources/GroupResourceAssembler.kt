package de.partspicker.web.project.api.resources

import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.DELETE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.generateGetAllGroupsLink
import de.partspicker.web.common.hal.withName
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
class GroupResourceAssembler : RepresentationModelAssembler<Group, GroupResource> {
    override fun toModel(group: Group): GroupResource {
        return GroupResource(
            id = group.id,
            name = group.name!!,
            description = group.description,
            links = generateDefaultLinks(groupId = group.id, orgUnitId = group.orgUnitId)
        )
    }

    private fun generateDefaultLinks(groupId: Long, orgUnitId: Long): List<Link> {
        return listOf(
            linkTo<GroupController> { handleGetGroupById(groupId) }
                .withSelfRel()
                .withName(READ),
            linkTo<GroupController> { handlePutGroup(groupId, GroupPutRequest.DUMMY) }
                .withSelfRel()
                .withName(UPDATE),
            linkTo<GroupController> { handleDeleteGroup(groupId) }
                .withSelfRel()
                .withName(DELETE),
            generateGetAllGroupsLink(IanaLinkRelations.COLLECTION, orgUnitId),
            linkTo<GroupController> { handlePostGroup(orgUnitId, GroupPostRequest.DUMMY) }
                .withRel(IanaLinkRelations.COLLECTION)
                .withName(CREATE)
        )
    }
}
