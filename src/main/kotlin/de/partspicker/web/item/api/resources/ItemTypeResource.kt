package de.partspicker.web.item.api.resources

import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = ItemTypeResource.collectionRelationName)
class ItemTypeResource(
    val name: String,
    val description: String,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<ItemTypeResource>(links) {
    companion object {
        const val collectionRelationName = "itemTypes"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ItemTypeResource

        if (name != other.name) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        return result
    }

    override fun toString(): String {
        return "ItemTypeResource(name='$name', description='$description')"
    }
}
