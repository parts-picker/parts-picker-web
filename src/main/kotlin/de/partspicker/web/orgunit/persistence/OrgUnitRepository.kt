package de.partspicker.web.orgunit.persistence

import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrgUnitRepository : JpaRepository<OrgUnitEntity, Long> {

    companion object {
        const val OWNER_NAME_CONSTRAINT = "uq_org_units_owner_name"
    }

    /**
     * Loads the org unit together with its owner & its creator, both of which the full view names.
     */
    @EntityGraph(attributePaths = ["owner", "creation.createdBy"])
    fun findWithOwnerAndCreatorById(id: Long): OrgUnitEntity?
}
