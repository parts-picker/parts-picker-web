package de.parts_picker.web.item.api.resources

import de.parts_picker.web.common.hal.withMethods
import de.parts_picker.web.item.api.ItemController
import de.parts_picker.web.item.api.responses.ItemConditionResponse
import de.parts_picker.web.item.api.responses.ItemStatusResponse
import de.parts_picker.web.item.business.objects.Item
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpMethod

class ItemResource(
    val id: Long,
    val status: ItemStatusResponse,
    val condition: ItemConditionResponse,
    val note: String?,
    links: Iterable<Link>
) : RepresentationModel<ItemResource>(links) {

    companion object {
        fun from(item: Item, vararg links: Link): ItemResource {
            val combinedLinks = generateDefaultLinks(itemId = item.id!!) + links

            return ItemResource(
                id = item.id,
                links = combinedLinks,
                status = ItemStatusResponse.from(item.status),
                condition = ItemConditionResponse.from(item.condition),
                note = item.note
            )
        }

        private fun generateDefaultLinks(itemId: Long): List<Link> {
            return listOf(
                linkTo<ItemController> { handleGetAllItems() }.withRel(IanaLinkRelations.COLLECTION)
                    .withMethods(HttpMethod.GET),
                linkTo<ItemController> { handleGetItemById(itemId) }.withSelfRel()
                    .withMethods(HttpMethod.GET)
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
