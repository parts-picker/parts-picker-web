package de.partspicker.web.project.api.resources

import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = ProjectResource.COLLECTION_RELATION_NAME)
@Suppress("LongParameterList")
class ProjectResource(
    val id: Long,
    val name: String,
    val status: String,
    val displayStatus: String,
    val shortDescription: String?,
    val description: String?,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<ProjectResource>(links) {

    companion object {
        const val COLLECTION_RELATION_NAME = "projects"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ProjectResource

        if (id != other.id) return false
        if (name != other.name) return false
        if (shortDescription != other.shortDescription) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + shortDescription.hashCode()
        result = 31 * result + description.hashCode()
        return result
    }

    override fun toString(): String {
        return "ProjectResource(id=$id, name='$name', shortDescription=$shortDescription," +
            " description=$description)"
    }
}
