package de.partspicker.web.item.api.resources

import de.partspicker.web.item.api.responses.ItemConditionResponse
import de.partspicker.web.item.api.responses.ItemStatusResponse
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

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
