package de.partspicker.web.inventory.api.resources

import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.LinkListBuilder
import de.partspicker.web.common.hal.RelationName.ASSIGNED
import de.partspicker.web.common.hal.RelationName.SUBSET_OF
import de.partspicker.web.common.hal.generateSearchItemsByNameLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.inventory.api.RequiredItemTypeController
import de.partspicker.web.inventory.api.requests.RequiredItemTypePostRequest
import de.partspicker.web.inventory.business.objects.AvailableItemType
import de.partspicker.web.item.api.ItemTypeController
import org.springframework.hateoas.IanaLinkRelations.COLLECTION
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class AvailableItemTypeResourceAssembler :
    RepresentationModelAssembler<AvailableItemType, AvailableItemTypeResource> {
    override fun toModel(availableItemType: AvailableItemType): AvailableItemTypeResource {
        return AvailableItemTypeResource(
            name = availableItemType.name,
            links = generateDefaultLinks(availableItemType)
        )
    }

    private fun generateDefaultLinks(availableItemType: AvailableItemType): List<Link> {
        return LinkListBuilder()
            .with(
                linkTo<ItemTypeController> { handleGetItemTypeById(availableItemType.id) }
                    .withRel(SUBSET_OF)
                    .withName(READ),
            ).with(
                linkTo<RequiredItemTypeController> {
                    handlePostRequiredItemTypes(
                        availableItemType.projectId,
                        availableItemType.id,
                        RequiredItemTypePostRequest.DUMMY
                    )
                }
                    .withRel(ASSIGNED)
                    .withName(CREATE),
                NodeNameEqualsRule(availableItemType.projectStatus, "planning")
            ).with(
                generateSearchItemsByNameLink(COLLECTION, availableItemType.projectId)
            )
            .build()
    }
}
