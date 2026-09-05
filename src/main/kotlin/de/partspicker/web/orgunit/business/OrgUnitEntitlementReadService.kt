package de.partspicker.web.orgunit.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.orgunit.persistence.OrgUnitEntitlementRepository
import org.springframework.stereotype.Service

@Service
class OrgUnitEntitlementReadService(
    private val orgUnitEntitlementRepository: OrgUnitEntitlementRepository
) {
    /**
     * The level the given user holds within the given org unit, or [AccessLevel.NONE] if they hold none.
     */
    fun accessLevelOf(orgUnitId: Long, userId: Long) = AccessLevel.from(
        this.orgUnitEntitlementRepository.findByOrgUnitIdAndUserId(orgUnitId, userId)?.accessLevel
    )
}
