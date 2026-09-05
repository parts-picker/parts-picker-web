package de.partspicker.web.orgunit.business.rules

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.business.rules.Rule
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.orgunit.business.exceptions.OrgUnitAccessDeniedException

class OrgUnitAccessRule(
    private val grantedLevel: AccessLevel,
    private val requiredLevel: AccessLevel,
    private val orgUnitId: Long
) : Rule {
    override fun valid() {
        (grantedLevel isAtLeast requiredLevel) elseThrow OrgUnitAccessDeniedException(orgUnitId, requiredLevel)
    }
}
