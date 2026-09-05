package de.partspicker.web.orgunit.api.resources

import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = OrgUnitResource.COLLECTION_RELATION_NAME)
class OrgUnitSummaryResource(
    val name: String,
    val shortDescription: String?,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<OrgUnitSummaryResource>(links) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as OrgUnitSummaryResource

        if (name != other.name) return false
        if (shortDescription != other.shortDescription) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + shortDescription.hashCode()
        return result
    }

    override fun toString(): String {
        return "OrgUnitSummaryResource(name='$name', shortDescription=$shortDescription)"
    }
}
