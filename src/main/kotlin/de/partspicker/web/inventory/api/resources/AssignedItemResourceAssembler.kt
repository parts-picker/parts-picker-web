package de.partspicker.web.inventory.api.resources

import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.LinkListBuilder
import de.partspicker.web.common.hal.RelationName.ASSIGNED_TO
import de.partspicker.web.common.hal.RelationName.SUBSET_OF
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.inventory.api.InventoryItemController
import de.partspicker.web.inventory.api.responses.InventoryItemConditionResponse
import de.partspicker.web.inventory.business.objects.AssignedItem
import de.partspicker.web.item.api.ItemController
import de.partspicker.web.project.api.ProjectController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class AssignedItemResourceAssembler : RepresentationModelAssembler<AssignedItem, AssignedItemResource> {
    override fun toModel(assignedItem: AssignedItem): AssignedItemResource {
        return AssignedItemResource(
            condition = InventoryItemConditionResponse.from(assignedItem.condition),
            links = generateDefaultLinks(assignedItem)
        )
    }
}

private fun generateDefaultLinks(assignedItem: AssignedItem): List<Link> {
    return LinkListBuilder()
        .with(
            linkTo<ItemController> { handleGetItemById(assignedItem.itemId) }
                .withRel(SUBSET_OF)
                .withName(READ)
        ).with(
            linkTo<ProjectController> { handleGetProjectById(assignedItem.projectId) }
                .withRel(ASSIGNED_TO)
                .withName(READ)
        ).with(
            linkTo<InventoryItemController> { handlePatchAssignedItem(assignedItem.itemId) }
                .withRel(ASSIGNED_TO)
                .withName(UPDATE),
            NodeNameEqualsRule(assignedItem.projectStatus, "planning")
        ).build()
}
