package de.partspicker.web.orgunit.business.rules

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.business.rules.Rule
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.orgunit.business.exceptions.CreatorOrOrgUnitAccessDeniedException

/**
 * Checks whether a member is allowed to execute an action against an object in the org unit
 * with the given [orgUnitId].
 * Is valid when the user is a member of the org unit and is the creator of the object or has at least [requiredLevel].
 */
class CreatorOrOrgUnitAccessRule(
    private val objectCreatedById: Long,
    private val currentUserId: Long,
    private val grantedLevel: AccessLevel,
    private val requiredLevel: AccessLevel,
    private val orgUnitId: Long
) : Rule {
    override fun valid() {
        val isMember = this.grantedLevel != AccessLevel.NONE
        val isCreator = this.currentUserId == this.objectCreatedById

        (isMember && (isCreator || this.grantedLevel isAtLeast this.requiredLevel)) elseThrow
            CreatorOrOrgUnitAccessDeniedException(this.orgUnitId, this.requiredLevel)
    }
}
