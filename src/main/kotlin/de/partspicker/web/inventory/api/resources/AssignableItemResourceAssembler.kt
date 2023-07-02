package de.partspicker.web.inventory.api.resources

import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.common.hal.DefaultName
import de.partspicker.web.common.hal.LinkListBuilder
import de.partspicker.web.common.hal.RelationName
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.inventory.api.InventoryItemController
import de.partspicker.web.inventory.api.responses.InventoryItemConditionResponse
import de.partspicker.web.inventory.business.objects.AssignableItem
import de.partspicker.web.inventory.business.rules.RequiredGreaterAssignedAmountRule
import de.partspicker.web.item.api.ItemController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class AssignableItemResourceAssembler : RepresentationModelAssembler<AssignableItem, AssignableItemResource> {
    override fun toModel(assignableItem: AssignableItem): AssignableItemResource {
        return AssignableItemResource(
            condition = InventoryItemConditionResponse.from(assignableItem.condition),
            links = generateDefaultLinks(assignableItem)
        )
    }

    private fun generateDefaultLinks(assignableItem: AssignableItem): List<Link> {
        return LinkListBuilder()
            .with(
                linkTo<ItemController> { handleGetItemById(assignableItem.itemId) }
                    .withRel(RelationName.SUBSET_OF)
                    .withName(DefaultName.READ)
            ).with(
                linkTo<InventoryItemController> {
                    handlePatchAssignableItem(
                        assignableItem.assignableToProjectId,
                        assignableItem.itemId
                    )
                }
                    .withRel(RelationName.ASSIGNED_TO)
                    .withName(DefaultName.UPDATE),
                RequiredGreaterAssignedAmountRule(assignableItem.requiredAmount, assignableItem.assignedAmount),
                NodeNameEqualsRule(assignableItem.assignableToProjectStatus, "planning")
            ).build()
    }
}
