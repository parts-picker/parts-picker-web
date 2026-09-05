package de.partspicker.web.orgunit.api.resources

import de.partspicker.web.orgunit.api.responses.AccessLevelResponse
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.time.Instant

@Relation(collectionRelation = OrgUnitMembershipResource.COLLECTION_RELATION_NAME)
class OrgUnitMembershipResource(
    val orgUnit: OrgUnitSummaryResource,
    val accessLevel: AccessLevelResponse,
    val joinedOn: Instant,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<OrgUnitMembershipResource>(links) {

    companion object {
        const val COLLECTION_RELATION_NAME = "orgUnitMemberships"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as OrgUnitMembershipResource

        if (orgUnit != other.orgUnit) return false
        if (accessLevel != other.accessLevel) return false
        if (joinedOn != other.joinedOn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + orgUnit.hashCode()
        result = 31 * result + accessLevel.hashCode()
        result = 31 * result + joinedOn.hashCode()
        return result
    }

    override fun toString(): String {
        return "OrgUnitMembershipResource(orgUnit=$orgUnit, accessLevel=$accessLevel, joinedOn=$joinedOn)"
    }
}
