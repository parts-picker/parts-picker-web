package de.partspicker.web.inventory.api.resources

import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.DELETE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.RelationName.ASSIGNABLE
import de.partspicker.web.common.hal.RelationName.ASSIGNED
import de.partspicker.web.common.hal.RelationName.ASSIGNED_TO
import de.partspicker.web.common.hal.generateGetAllAssignableItemsLink
import de.partspicker.web.common.hal.generateGetAllAssignedItemsLink
import de.partspicker.web.common.hal.generateGetAllRequiredItemTypesLink
import de.partspicker.web.common.hal.withName
import de.partspicker.web.common.hal.withRel
import de.partspicker.web.inventory.api.RequiredItemTypeController
import de.partspicker.web.inventory.api.requests.RequiredItemTypePatchRequest
import de.partspicker.web.inventory.api.requests.RequiredItemTypePostRequest
import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.project.api.ProjectController
import org.springframework.hateoas.IanaLinkRelations.COLLECTION
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
            assignedAmount = requiredItemType.assignedAmount,
            requiredAmount = requiredItemType.requiredAmount,
            links = generateDefaultLinks(
                projectId = requiredItemType.projectId,
                itemTypeId = requiredItemType.itemType.id
            ),
        )
    }

    private fun generateDefaultLinks(projectId: Long, itemTypeId: Long): List<Link> {
        return listOf(
            linkTo<ProjectController> { handleGetProjectById(projectId) }
                .withRel(ASSIGNED_TO)
                .withName(READ),
            linkTo<ItemTypeController> { handleGetItemTypeById(itemTypeId) }
                .withRel(DESCRIBED_BY)
                .withName(READ),
            // self-related links
            linkTo<RequiredItemTypeController> {
                handlePostRequiredItemTypes(projectId, itemTypeId, RequiredItemTypePostRequest.DUMMY)
            }
                .withRel(COLLECTION)
                .withName(CREATE),
            generateGetAllRequiredItemTypesLink(COLLECTION, projectId),
            linkTo<RequiredItemTypeController> {
                handlePatchByProjectIdAndItemTypeId(projectId, itemTypeId, RequiredItemTypePatchRequest.DUMMY)
            }
                .withSelfRel()
                .withName(UPDATE),
            linkTo<RequiredItemTypeController> { handleDeleteByProjectIdAndItemTypeId(projectId, itemTypeId) }
                .withSelfRel()
                .withName(DELETE),
            // item related links
            generateGetAllAssignableItemsLink(ASSIGNABLE, projectId, itemTypeId),
            generateGetAllAssignedItemsLink(ASSIGNED, projectId, itemTypeId),
        )
    }
}
