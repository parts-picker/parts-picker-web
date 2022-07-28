package de.partspicker.web.item.api.resources

import de.partspicker.web.common.hal.DefaultName
import de.partspicker.web.common.hal.generateGetAllItemsLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.item.api.ItemController
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.item.api.requests.ItemPostRequest
import de.partspicker.web.item.api.requests.ItemPutRequest
import de.partspicker.web.item.api.responses.ItemConditionResponse
import de.partspicker.web.item.api.responses.ItemStatusResponse
import de.partspicker.web.item.business.objects.Item
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
                itemTypeId = item.type.id
            )
        )
    }

    private fun generateDefaultLinks(itemId: Long, itemTypeId: Long): List<Link> {
        return listOf(
            linkTo<ItemController> { handleGetItemById(itemId) }
                .withSelfRel()
                .withName(DefaultName.READ),
            generateGetAllItemsLink(IanaLinkRelations.SELF),
            linkTo<ItemTypeController> { handleGetItemTypeById(itemTypeId) }
                .withRel(IanaLinkRelations.DESCRIBED_BY)
                .withName(DefaultName.READ),
            linkTo<ItemController> { handlePostItem(itemTypeId, ItemPostRequest.DUMMY) }
                .withRel(IanaLinkRelations.COLLECTION)
                .withName(DefaultName.CREATE),
            linkTo<ItemController> { handleDeleteItemById(itemId) }
                .withSelfRel()
                .withName(DefaultName.DELETE),
            linkTo<ItemController> { handlePutItemById(itemId, ItemPutRequest.DUMMY) }
                .withSelfRel()
                .withName(DefaultName.UPDATE)
        )
    }
}
