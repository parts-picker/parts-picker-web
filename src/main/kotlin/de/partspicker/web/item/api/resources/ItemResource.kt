package de.partspicker.web.item.api.resources

import de.partspicker.web.common.hal.DefaultName.CREATE
import de.partspicker.web.common.hal.DefaultName.READ
import de.partspicker.web.common.hal.withName
import de.partspicker.web.item.api.ItemController
import de.partspicker.web.item.api.ItemTypeController
import de.partspicker.web.item.api.requests.ItemPostRequest
import de.partspicker.web.item.api.responses.ItemConditionResponse
import de.partspicker.web.item.api.responses.ItemStatusResponse
import de.partspicker.web.item.business.objects.Item
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.mvc.linkTo

@Relation(collectionRelation = ItemResource.collectionRelationName)
class ItemResource(
    val id: Long,
    val status: ItemStatusResponse,
    val condition: ItemConditionResponse,
    val note: String?,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<ItemResource>(links) {

    companion object {
        const val collectionRelationName = "items"

        fun from(item: Item, vararg links: Link): ItemResource {
            val combinedLinks = generateDefaultLinks(
                itemId = item.id!!,
                itemTypeId = item.type.id!!
            ) + links

            return ItemResource(
                id = item.id,
                status = ItemStatusResponse.from(item.status),
                condition = ItemConditionResponse.from(item.condition),
                note = item.note,
                links = combinedLinks
            )
        }

        private fun generateDefaultLinks(itemId: Long, itemTypeId: Long): List<Link> {
            return listOf(
                linkTo<ItemController> { handleGetItemById(itemId) }
                    .withSelfRel()
                    .withName(READ),
                linkTo<ItemController> { handleGetAllItems() }
                    .withRel(IanaLinkRelations.COLLECTION)
                    .withName(READ),
                linkTo<ItemTypeController> { handleGetItemTypeById(itemTypeId) }
                    .withRel(IanaLinkRelations.DESCRIBED_BY)
                    .withName(READ),
                linkTo<ItemController> { handlePostItem(ItemPostRequest.DUMMY) }
                    .withRel(IanaLinkRelations.COLLECTION)
                    .withName(CREATE)
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ItemResource

        if (id != other.id) return false
        if (status != other.status) return false
        if (condition != other.condition) return false
        if (note != other.note) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + condition.hashCode()
        result = 31 * result + (note?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "ItemResource(id=$id, status=$status, condition=$condition, note=$note, links=$links)"
    }
}
