package de.partspicker.web.common.hal

import de.partspicker.web.item.api.ItemController
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.item.api.requests.ItemPostRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.server.mvc.linkTo

fun generateGetAllItemTypesLink(relation: LinkRelation, orgUnitId: Long) =
    generateGetAllItemTypesLink(relation.toString(), orgUnitId)

fun generateGetAllItemTypesLink(relation: String, orgUnitId: Long): Link {
    // READ itemTypes link with page meta information params
    val uriTemplate = UriTemplate.of(
        linkTo<ItemTypeController> { handleGetAllItemTypes(orgUnitId, Pageable.unpaged()) }.toUri().toString()
    ).withPaginationParams()

    return Link.of(uriTemplate, relation).withName(DefaultName.READ)
}

fun generateGetAllItemsLink(relation: LinkRelation, orgUnitId: Long) =
    generateGetAllItemsLink(relation.toString(), orgUnitId)

fun generateGetAllItemsLink(relation: String, orgUnitId: Long): Link {
    // READ items link with page meta information params
    val uriTemplate = UriTemplate.of(
        linkTo<ItemController> { handleGetAllItems(orgUnitId, Pageable.unpaged()) }.toUri().toString()
    ).withPaginationParams()

    return Link.of(uriTemplate, relation).withName(DefaultName.READ)
}

fun generateGetAllItemsByItemTypeIdLink(relation: LinkRelation, itemTypeId: Long) =
    generateGetAllItemsByItemTypeIdLink(relation.toString(), itemTypeId)

fun generateGetAllItemsByItemTypeIdLink(relation: String, itemTypeId: Long): Link {
    // READ items link with page meta information params
    val uriTemplate = UriTemplate.of(
        linkTo<ItemController> { handleGetItemsByItemTypeId(itemTypeId, Pageable.unpaged()) }.toUri().toString()
    ).withPaginationParams()

    return Link.of(uriTemplate, relation).withName(DefaultName.READ)
}

fun generatePostItemLink(relation: LinkRelation, itemTypeId: Long) =
    generatePostItemLink(relation.toString(), itemTypeId)

fun generatePostItemLink(relation: String, itemTypeId: Long): Link =
    linkTo<ItemController> { handlePostItem(itemTypeId, ItemPostRequest.DUMMY) }
        .withRel(relation)
        .withName(DefaultName.CREATE)
