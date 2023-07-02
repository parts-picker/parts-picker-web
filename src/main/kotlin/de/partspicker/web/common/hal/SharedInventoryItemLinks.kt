package de.partspicker.web.common.hal

import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.inventory.api.InventoryItemController
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.server.mvc.linkTo

fun generateGetAllAssignableItemsLink(relation: RelationName, projectId: Long, itemTypeId: Long) =
    generateGetAllAssignableItemsLink(relation.displayName, projectId, itemTypeId)

fun generateGetAllAssignableItemsLink(relation: LinkRelation, projectId: Long, itemTypeId: Long) =
    generateGetAllAssignableItemsLink(relation.toString(), projectId, itemTypeId)

fun generateGetAllAssignableItemsLink(relation: String, projectId: Long, itemTypeId: Long): Link {
    // READ assignable items link with page meta information params
    val uriTemplate = UriTemplate.of(
        linkTo<InventoryItemController> {
            handleGetAllAssignableItems(projectId, itemTypeId, Pageable.unpaged())
        }.toUri().toString()
    ).withPaginationParams()

    return Link.of(uriTemplate, relation).withName(READ)
}

fun generateGetAllAssignedItemsLink(relation: RelationName, projectId: Long, itemTypeId: Long) =
    generateGetAllAssignedItemsLink(relation.displayName, projectId, itemTypeId)

fun generateGetAllAssignedItemsLink(relation: LinkRelation, projectId: Long, itemTypeId: Long) =
    generateGetAllAssignedItemsLink(relation.toString(), projectId, itemTypeId)

fun generateGetAllAssignedItemsLink(relation: String, projectId: Long, itemTypeId: Long): Link {
    // READ assigned items link with page meta information params
    val uriTemplate = UriTemplate.of(
        linkTo<InventoryItemController> {
            handleGetAllAssignedItems(projectId, itemTypeId, Pageable.unpaged())
        }.toUri().toString()
    ).withPaginationParams()

    return Link.of(uriTemplate, relation).withName(READ)
}
