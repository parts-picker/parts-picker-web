package de.partspicker.web.orgunit.persistence

import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntitlementEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrgUnitEntitlementRepository : JpaRepository<OrgUnitEntitlementEntity, Long> {
    fun findByOrgUnitIdAndUserId(orgUnitId: Long, userId: Long): OrgUnitEntitlementEntity?

    fun existsByOrgUnitIdAndUserId(orgUnitId: Long, userId: Long): Boolean
}
