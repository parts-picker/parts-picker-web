package de.partspicker.web.project.api.resources

import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = ProjectResource.collectionRelationName)
@Suppress("LongParameterList")
class ProjectResource(
    val id: Long,
    val name: String,
    val status: String,
    val shortDescription: String?,
    val description: String?,
    var groupId: Long?,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<ProjectResource>(links) {

    companion object {
        const val collectionRelationName = "projects"
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
        if (groupId != other.groupId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (shortDescription?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (groupId?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "ProjectResource(id=$id, name='$name', shortDescription=$shortDescription," +
            " description=$description, groupId=$groupId)"
    }
}
