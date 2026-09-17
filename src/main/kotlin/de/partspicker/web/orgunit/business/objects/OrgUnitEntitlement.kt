package de.partspicker.web.orgunit.business.objects

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntitlementEntity
import de.partspicker.web.user.business.objects.User
import java.time.Instant

data class OrgUnitEntitlement(
    val id: Long,
    val orgUnit: OrgUnitSummary,
    val user: User,
    val accessLevel: AccessLevel,
    val joinedOn: Instant
) {
    companion object {
        fun from(orgUnitEntitlementEntity: OrgUnitEntitlementEntity) = OrgUnitEntitlement(
            id = orgUnitEntitlementEntity.id,
            orgUnit = OrgUnitSummary.from(orgUnitEntitlementEntity.orgUnit),
            user = User.from(orgUnitEntitlementEntity.user),
            accessLevel = AccessLevel.from(orgUnitEntitlementEntity.accessLevel),
            joinedOn = orgUnitEntitlementEntity.joinedOn
        )
    }
}
