package de.partspicker.web.project.api.resources

import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = GroupResource.COLLECTION_RELATION_NAME)
class GroupResource(
    val id: Long,
    val name: String,
    val description: String?,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<GroupResource>(links) {

    companion object {
        const val COLLECTION_RELATION_NAME = "groups"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as GroupResource

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "GroupResource(id=$id, name='$name', description=$description)"
    }
}
