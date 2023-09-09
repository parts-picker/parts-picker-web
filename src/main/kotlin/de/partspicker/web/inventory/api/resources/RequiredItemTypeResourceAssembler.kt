package de.partspicker.web.inventory.api.resources

import de.partspicker.web.common.business.rules.NodeNameEqualsRule
import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.DELETE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.DefaultName.UPDATE
import de.partspicker.web.common.hal.LinkListBuilder
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
                requiredItemType = requiredItemType
            ),
        )
    }

    @Suppress("LongMethod")
    private fun generateDefaultLinks(requiredItemType: RequiredItemType): List<Link> {
        val projectStatusPlanningRule = NodeNameEqualsRule(requiredItemType.projectStatus, "planning")

        return LinkListBuilder()
            .with(
                linkTo<ProjectController> { handleGetProjectById(requiredItemType.projectId) }
                    .withRel(ASSIGNED_TO)
                    .withName(READ)
            )
            .with(
                linkTo<ItemTypeController> { handleGetItemTypeById(requiredItemType.itemType.id) }
                    .withRel(DESCRIBED_BY)
                    .withName(READ)
            )
            // collection related links
            .with(
                linkTo<RequiredItemTypeController> {
                    handlePostRequiredItemTypes(
                        requiredItemType.projectId,
                        requiredItemType.itemType.id,
                        RequiredItemTypePostRequest.DUMMY
                    )
                }
                    .withRel(COLLECTION)
                    .withName(CREATE),
                projectStatusPlanningRule,
            )
            .with(generateGetAllRequiredItemTypesLink(COLLECTION, requiredItemType.projectId))
            // self-related links
            .with(
                linkTo<RequiredItemTypeController> {
                    handlePatchByProjectIdAndItemTypeId(
                        requiredItemType.projectId,
                        requiredItemType.itemType.id,
                        RequiredItemTypePatchRequest.DUMMY
                    )
                }
                    .withSelfRel()
                    .withName(UPDATE),
                projectStatusPlanningRule
            )
            .with(
                linkTo<RequiredItemTypeController> {
                    handleDeleteByProjectIdAndItemTypeId(
                        requiredItemType.projectId,
                        requiredItemType.itemType.id
                    )
                }
                    .withSelfRel()
                    .withName(DELETE),
                projectStatusPlanningRule
            )
            // item related links
            .with(
                generateGetAllAssignableItemsLink(
                    ASSIGNABLE,
                    requiredItemType.projectId,
                    requiredItemType.itemType.id
                )
            )
            .with(
                generateGetAllAssignedItemsLink(
                    ASSIGNED,
                    requiredItemType.projectId,
                    requiredItemType.itemType.id
                )
            )
            .build()
    }
}
