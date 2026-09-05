package de.partspicker.web.orgunit.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.business.rules.Rule
import de.partspicker.web.common.security.CurrentUserProvider
import de.partspicker.web.common.security.SystemContext
import de.partspicker.web.orgunit.business.rules.CreatorOrOrgUnitAccessRule
import de.partspicker.web.orgunit.business.rules.OrgUnitAccessRule
import org.springframework.stereotype.Service

/**
 * Checks whether the user of the current request may act within an org unit.
 */
@Service
class OrgUnitAccessService(
    private val orgUnitEntitlementReadService: OrgUnitEntitlementReadService,
    private val currentUserProvider: CurrentUserProvider,
    private val systemContext: SystemContext
) {

    fun levelIn(orgUnitId: Long): AccessLevel =
        this.orgUnitEntitlementReadService.accessLevelOf(orgUnitId, this.currentUserProvider.getCurrentUser().id)

    /**
     * Checks if the current user has at least the required level of access to the org unit with the given id.
     * If run as system, all checks are skipped.
     */
    fun requireAtLeast(orgUnitId: Long, requiredLevel: AccessLevel) {
        if (this.systemContext.isSystem()) {
            return
        }

        this.atLeast(orgUnitId, requiredLevel).valid()
    }

    /**
     * Checks whether a member is allowed to execute an action against an object in the org unit
     * with the given [orgUnitId].
     * Is valid when the user is the creator or has at least [requiredLevel].
     * If run as system, all checks are skipped.
     */
    fun requireMemberCreatorOrAtLeast(orgUnitId: Long, objectCreatedById: Long, requiredLevel: AccessLevel) {
        if (this.systemContext.isSystem()) {
            return
        }

        this.memberCreatorOrAtLeast(orgUnitId, objectCreatedById, requiredLevel).valid()
    }

    /**
     * The rule behind [requireAtLeast], for deciding whether to offer an action.
     */
    fun atLeast(orgUnitId: Long, requiredLevel: AccessLevel): Rule =
        OrgUnitAccessRule(this.levelIn(orgUnitId), requiredLevel, orgUnitId)

    /**
     * The rule behind [requireMemberCreatorOrAtLeast], for deciding whether to offer an action.
     */
    fun memberCreatorOrAtLeast(orgUnitId: Long, objectCreatedById: Long, requiredLevel: AccessLevel): Rule =
        CreatorOrOrgUnitAccessRule(
            objectCreatedById = objectCreatedById,
            currentUserId = this.currentUserProvider.getCurrentUser().id,
            grantedLevel = this.levelIn(orgUnitId),
            requiredLevel = requiredLevel,
            orgUnitId = orgUnitId
        )
}
