package de.partspicker.web.orgunit.api.resources

import de.partspicker.web.user.api.resources.UserResource
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.time.Instant

@Relation(collectionRelation = OrgUnitResource.COLLECTION_RELATION_NAME)
class OrgUnitResource(
    val name: String,
    val shortDescription: String?,
    val owner: UserResource,
    val createdBy: UserResource,
    val createdOn: Instant,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<OrgUnitResource>(links) {

    companion object {
        const val COLLECTION_RELATION_NAME = "orgUnits"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as OrgUnitResource

        if (name != other.name) return false
        if (shortDescription != other.shortDescription) return false
        if (owner != other.owner) return false
        if (createdBy != other.createdBy) return false
        if (createdOn != other.createdOn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + shortDescription.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + createdBy.hashCode()
        result = 31 * result + createdOn.hashCode()
        return result
    }

    override fun toString(): String {
        return "OrgUnitResource(name='$name', shortDescription=$shortDescription, owner=$owner," +
            " createdBy=$createdBy, createdOn=$createdOn)"
    }
}
