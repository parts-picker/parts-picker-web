package de.partspicker.web.orgunit.business.rules

import de.partspicker.web.common.business.exceptions.CrossOrgUnitReferenceException
import de.partspicker.web.common.business.rules.Rule
import de.partspicker.web.common.util.elseThrow

/**
 * Valid if both org unit ids match.
 */
class SameOrgUnitRule(
    private val firstOrgUnitId: Long,
    private val secondOrgUnitId: Long
) : Rule {
    override fun valid() {
        (this.firstOrgUnitId == this.secondOrgUnitId) elseThrow
            CrossOrgUnitReferenceException(this.firstOrgUnitId, this.secondOrgUnitId)
    }
}
