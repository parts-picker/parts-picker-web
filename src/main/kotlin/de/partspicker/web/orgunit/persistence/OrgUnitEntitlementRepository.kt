package de.partspicker.web.orgunit.persistence

import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntitlementEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrgUnitEntitlementRepository : JpaRepository<OrgUnitEntitlementEntity, Long> {

    companion object {
        const val ORG_UNIT_USER_CONSTRAINT = "uq_entitlements_org_unit_user"
    }

    fun findByOrgUnitIdAndUserId(orgUnitId: Long, userId: Long): OrgUnitEntitlementEntity?

    /**
     * Loads every entitlement of the given user together with its org unit.
     */
    @EntityGraph(attributePaths = ["orgUnit"])
    fun findAllWithOrgUnitByUserId(userId: Long, pageable: Pageable): Page<OrgUnitEntitlementEntity>
}
