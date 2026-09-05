package de.partspicker.web.inventory.api.resources

import de.partspicker.web.inventory.api.resources.AvailableItemTypeResource.Companion.COLLECTION_RELATION_NAME
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = COLLECTION_RELATION_NAME)
class AvailableItemTypeResource(
    val name: String,
    links: Iterable<Link> = emptyList()

) : RepresentationModel<AvailableItemTypeResource>(links) {
    companion object {
        const val COLLECTION_RELATION_NAME = "availableItemTypes"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AvailableItemTypeResource

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "AvailableItemTypeResource(name='$name')"
    }
}
