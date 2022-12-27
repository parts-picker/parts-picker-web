package de.partspicker.web.workflow.api.resources.nodes

import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = NodeResource.collectionRelationName)
sealed class NodeResource(
    val id: Long,
    val name: String,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<NodeResource>(links) {
    companion object {
        const val collectionRelationName = "nodes"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NodeResource

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "NodeResource(id=$id, name='$name')"
    }
}
