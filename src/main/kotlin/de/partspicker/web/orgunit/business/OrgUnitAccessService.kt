package de.partspicker.web.orgunit.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.security.CurrentUserProvider
import de.partspicker.web.orgunit.business.rules.CreatorOrOrgUnitAccessRule
import de.partspicker.web.orgunit.business.rules.OrgUnitAccessRule
import de.partspicker.web.user.persistence.entities.UserEntity
import org.springframework.stereotype.Service

/**
 * Checks whether the user of the current request may act within an org unit.
 */
@Service
class OrgUnitAccessService(
    private val orgUnitEntitlementReadService: OrgUnitEntitlementReadService,
    private val currentUserProvider: CurrentUserProvider
) {

    fun currentUser(): UserEntity = this.currentUserProvider.getCurrentUserEntity()

    fun levelIn(orgUnitId: Long): AccessLevel =
        this.orgUnitEntitlementReadService.accessLevelOf(orgUnitId, this.currentUser().id)

    fun requireAtLeast(orgUnitId: Long, requiredLevel: AccessLevel) {
        OrgUnitAccessRule(this.levelIn(orgUnitId), requiredLevel, orgUnitId).valid()
    }

    /**
     * Checks whether a member is allowed to execute an action against an object in the org unit
     * with the given [orgUnitId].
     * Is valid when the user is the creator or has at least [requiredLevel].
     */
    fun requireMemberCreatorOrAtLeast(orgUnitId: Long, objectCreatedById: Long, requiredLevel: AccessLevel) {
        CreatorOrOrgUnitAccessRule(
            objectCreatedById = objectCreatedById,
            currentUserId = this.currentUser().id,
            grantedLevel = this.levelIn(orgUnitId),
            requiredLevel = requiredLevel,
            orgUnitId = orgUnitId
        ).valid()
    }
}
