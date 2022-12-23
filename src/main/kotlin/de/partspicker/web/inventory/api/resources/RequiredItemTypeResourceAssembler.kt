package de.partspicker.web.inventory.api.resources

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.RelationName.ASSIGNED_TO
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.project.api.ProjectController
import org.springframework.hateoas.IanaLinkRelations.DESCRIBED_BY
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class RequiredItemTypeResourceAssembler : RepresentationModelAssembler<RequiredItemType, RequiredItemTypeResource> {
    override fun toModel(requiredItemType: RequiredItemType): RequiredItemTypeResource {
        return RequiredItemTypeResource(
            itemTypeName = requiredItemType.itemType.name!!,
            requiredAmount = requiredItemType.requiredAmount,
            generateDefaultLinks(projectId = requiredItemType.projectId, itemTypeId = requiredItemType.itemType.id)
        )
    }

    private fun generateDefaultLinks(projectId: Long, itemTypeId: Long): List<Link> {
        return listOf(
            linkTo<ProjectController> { handleGetProjectById(projectId) }
                .withRel(ASSIGNED_TO)
                .withName(READ),
            linkTo<ItemTypeController> { handleGetItemTypeById(itemTypeId) }
                .withRel(DESCRIBED_BY)
                .withName(READ)
        )
    }
}
