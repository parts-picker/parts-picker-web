package de.partspicker.web.orgunit.persistence

import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrgUnitRepository : JpaRepository<OrgUnitEntity, Long> {

    /**
     * Loads the org unit together with its owner.
     */
    @EntityGraph(attributePaths = ["owner"])
    fun findWithOwnerById(id: Long): OrgUnitEntity?
}
