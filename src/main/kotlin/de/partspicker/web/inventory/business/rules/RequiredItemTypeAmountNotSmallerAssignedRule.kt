package de.partspicker.web.inventory.business.rules

import de.partspicker.web.common.business.rules.Rule
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.inventory.business.exceptions.RequiredItemTypeAmountSmallerThanAssignedException

class RequiredItemTypeAmountNotSmallerAssignedRule(
    private val requiredAmount: Long,
    private val assignedAmount: Long
) : Rule {
    override fun valid() {
        (requiredAmount >= assignedAmount) elseThrow
            RequiredItemTypeAmountSmallerThanAssignedException(requiredAmount, assignedAmount)
    }
}
