package de.partspicker.web.item.api.resources

import de.partspicker.web.common.business.objects.enums.AccessLevel.CONFIGURE
import de.partspicker.web.common.business.objects.enums.AccessLevel.MAINTAIN
import de.partspicker.web.common.business.objects.enums.AccessLevel.USE
import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.DELETE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.LinkListBuilder
import de.partspicker.web.common.hal.generateGetAllItemTypesLink
import de.partspicker.web.common.hal.generateGetAllItemsByItemTypeIdLink
import de.partspicker.web.common.hal.generatePostItemLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.item.api.requests.ItemTypePostRequest
import de.partspicker.web.item.api.requests.ItemTypePutRequest
import de.partspicker.web.item.business.objects.ItemType
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class ItemTypeResourceAssembler(
    private val orgUnitAccessService: OrgUnitAccessService
) : RepresentationModelAssembler<ItemType, ItemTypeResource> {
    override fun toModel(itemType: ItemType): ItemTypeResource {
        return ItemTypeResource(
            name = itemType.name!!,
            description = itemType.description!!,
            links = generateDefaultLinks(itemType)
        )
    }

    private fun generateDefaultLinks(itemType: ItemType): List<Link> {
        return LinkListBuilder()
            .with(
                linkTo<ItemTypeController> { handlePostItemType(itemType.orgUnitId, ItemTypePostRequest.DUMMY) }
                    .withRel(IanaLinkRelations.COLLECTION)
                    .withName(CREATE),
                this.orgUnitAccessService.atLeast(itemType.orgUnitId, CONFIGURE)
            )
            .with(
                linkTo<ItemTypeController> { handleGetItemTypeById(itemType.id) }
                    .withSelfRel()
                    .withName(READ)
            )
            .with(generateGetAllItemTypesLink(IanaLinkRelations.COLLECTION, itemType.orgUnitId))
            .with(
                linkTo<ItemTypeController> { handlePutItemTypeById(itemType.id, ItemTypePutRequest.DUMMY) }
                    .withSelfRel()
                    .withName(UPDATE),
                this.orgUnitAccessService.atLeast(itemType.orgUnitId, CONFIGURE)
            )
            .with(
                linkTo<ItemTypeController> { handleDeleteItemTypeById(itemType.id) }
                    .withSelfRel()
                    .withName(DELETE),
                this.orgUnitAccessService.memberCreatorOrAtLeast(itemType.orgUnitId, itemType.createdById, MAINTAIN)
            )
            .with(generateGetAllItemsByItemTypeIdLink(IanaLinkRelations.DESCRIBES, itemType.id))
            .with(
                generatePostItemLink(IanaLinkRelations.DESCRIBES, itemType.id),
                this.orgUnitAccessService.atLeast(itemType.orgUnitId, USE)
            )
            .build()
    }
}
