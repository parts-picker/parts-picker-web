package de.partspicker.web.orgunit.business.objects

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntitlementEntity
import java.time.Instant

/**
 * The entitlement of the user of the current request within one org unit.
 */
data class OrgUnitMembership(
    val orgUnit: OrgUnitSummary,
    val accessLevel: AccessLevel,
    val joinedOn: Instant
) {
    companion object {
        fun from(orgUnitEntitlementEntity: OrgUnitEntitlementEntity) = OrgUnitMembership(
            orgUnit = OrgUnitSummary.from(orgUnitEntitlementEntity.orgUnit),
            accessLevel = AccessLevel.from(orgUnitEntitlementEntity.accessLevel),
            joinedOn = orgUnitEntitlementEntity.joinedOn
        )
    }
}
