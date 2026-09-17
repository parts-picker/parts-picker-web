package de.partspicker.web.item.api.resources

import de.partspicker.web.common.business.objects.enums.AccessLevel.MAINTAIN
import de.partspicker.web.common.business.objects.enums.AccessLevel.USE
import de.partspicker.web.common.hal.DefaultName
import de.partspicker.web.common.hal.LinkListBuilder
import de.partspicker.web.common.hal.RelationName
import de.partspicker.web.common.hal.generateGetAllItemsLink
import de.partspicker.web.common.hal.generatePostItemLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.item.api.ItemController
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.item.api.requests.ItemGeneralPatchRequest
import de.partspicker.web.item.api.responses.ItemConditionResponse
import de.partspicker.web.item.api.responses.ItemStatusResponse
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.project.api.ProjectController
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class ItemResourceAssembler(
    private val orgUnitAccessService: OrgUnitAccessService
) : RepresentationModelAssembler<Item, ItemResource> {
    override fun toModel(item: Item): ItemResource {
        return ItemResource(
            id = item.id,
            status = ItemStatusResponse.from(item.status),
            condition = ItemConditionResponse.from(item.condition),
            note = item.note,
            links = generateDefaultLinks(item)
        )
    }

    private fun generateDefaultLinks(item: Item): List<Link> {
        return LinkListBuilder()
            .with(
                linkTo<ItemController> { handleGetItemById(item.id) }
                    .withSelfRel()
                    .withName(DefaultName.READ)
            )
            .with(generateGetAllItemsLink(IanaLinkRelations.COLLECTION, item.orgUnitId))
            .with(
                linkTo<ItemTypeController> { handleGetItemTypeById(item.type.id) }
                    .withRel(IanaLinkRelations.DESCRIBED_BY)
                    .withName(DefaultName.READ)
            )
            .with(
                generatePostItemLink(IanaLinkRelations.DESCRIBES, item.type.id),
                this.orgUnitAccessService.atLeast(item.orgUnitId, USE)
            )
            .with(
                linkTo<ItemController> { handleDeleteItemById(item.id) }
                    .withSelfRel()
                    .withName(DefaultName.DELETE),
                this.orgUnitAccessService.memberCreatorOrAtLeast(item.orgUnitId, item.createdById, MAINTAIN)
            )
            .with(
                linkTo<ItemController> { handlePatchItemById(item.id, ItemGeneralPatchRequest.DUMMY) }
                    .withSelfRel()
                    .withName(DefaultName.UPDATE),
                this.orgUnitAccessService.atLeast(item.orgUnitId, USE)
            )
            .withCondition(
                linkTo<ProjectController> { handleGetProjectById(item.assignedProjectId ?: 0L) }
                    .withRel(RelationName.ASSIGNED_TO)
                    .withName(DefaultName.READ),
                item.assignedProjectId != null
            )
            .build()
    }
}
