package de.partspicker.web.item.api.resources

import de.partspicker.web.common.hal.DefaultName
import de.partspicker.web.common.hal.RelationName
import de.partspicker.web.common.hal.generateGetAllItemsLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.item.api.ItemController
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.item.api.requests.ItemGeneralPatchRequest
import de.partspicker.web.item.api.requests.ItemPostRequest
import de.partspicker.web.item.api.responses.ItemConditionResponse
import de.partspicker.web.item.api.responses.ItemStatusResponse
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.project.api.ProjectController
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class ItemResourceAssembler : RepresentationModelAssembler<Item, ItemResource> {
    override fun toModel(item: Item): ItemResource {
        return ItemResource(
            id = item.id,
            status = ItemStatusResponse.from(item.status),
            condition = ItemConditionResponse.from(item.condition),
            note = item.note,
            links = generateDefaultLinks(
                itemId = item.id,
                itemTypeId = item.type.id,
                assignedProjectId = item.assignedProject?.id
            )
        )
    }

    private fun generateDefaultLinks(itemId: Long, itemTypeId: Long, assignedProjectId: Long?): List<Link> {
        val links = mutableListOf(
            linkTo<ItemController> { handleGetItemById(itemId) }
                .withSelfRel()
                .withName(DefaultName.READ),
            generateGetAllItemsLink(IanaLinkRelations.COLLECTION),
            linkTo<ItemTypeController> { handleGetItemTypeById(itemTypeId) }
                .withRel(IanaLinkRelations.DESCRIBED_BY)
                .withName(DefaultName.READ),
            linkTo<ItemController> { handlePostItem(itemTypeId, ItemPostRequest.DUMMY) }
                .withRel(IanaLinkRelations.COLLECTION)
                .withName(DefaultName.CREATE),
            linkTo<ItemController> { handleDeleteItemById(itemId) }
                .withSelfRel()
                .withName(DefaultName.DELETE),
            linkTo<ItemController> { handlePatchItemById(itemId, ItemGeneralPatchRequest.DUMMY) }
                .withSelfRel()
                .withName(DefaultName.UPDATE)
        )

        assignedProjectId?.let { projectId ->
            links.add(
                linkTo<ProjectController> { handleGetProjectById(projectId) }
                    .withRel(RelationName.ASSIGNED_TO)
                    .withName(DefaultName.READ)
            )
        }

        return links
    }
}
