package de.partspicker.web.item.api.resources

import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.DELETE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.generateGetAllItemTypesLink
import de.partspicker.web.common.hal.generateGetAllItemsByItemTypeIdLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.item.api.requests.ItemTypePostRequest
import de.partspicker.web.item.api.requests.ItemTypePutRequest
import de.partspicker.web.item.business.objects.ItemType
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class ItemTypeResourceAssembler : RepresentationModelAssembler<ItemType, ItemTypeResource> {
    override fun toModel(itemType: ItemType): ItemTypeResource {
        return ItemTypeResource(
            name = itemType.name!!,
            description = itemType.description!!,
            links = generateDefaultLinks(itemTypeId = itemType.id)
        )
    }

    private fun generateDefaultLinks(itemTypeId: Long): List<Link> {

        return listOf(
            linkTo<ItemTypeController> { handlePostItemType(ItemTypePostRequest.DUMMY) }
                .withRel(IanaLinkRelations.COLLECTION)
                .withName(CREATE),
            linkTo<ItemTypeController> { handleGetItemTypeById(itemTypeId) }
                .withSelfRel()
                .withName(READ),
            generateGetAllItemTypesLink(IanaLinkRelations.COLLECTION),
            linkTo<ItemTypeController> { handlePutItemTypeById(itemTypeId, ItemTypePutRequest.DUMMY) }
                .withSelfRel()
                .withName(UPDATE),
            linkTo<ItemTypeController> { handleDeleteItemTypeById(itemTypeId) }
                .withSelfRel()
                .withName(DELETE),
            generateGetAllItemsByItemTypeIdLink(IanaLinkRelations.DESCRIBES, itemTypeId)
        )
    }
}
