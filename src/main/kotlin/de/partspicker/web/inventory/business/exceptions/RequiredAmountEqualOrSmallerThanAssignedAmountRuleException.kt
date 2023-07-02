package de.partspicker.web.inventory.business.exceptions

import de.partspicker.web.common.business.exceptions.RuleException

class RequiredAmountEqualOrSmallerThanAssignedAmountRuleException(requiredAmount: Long) :
    RuleException("The required amount $requiredAmount is smaller than or equal to the assigned amount")
