package de.partspicker.web.inventory.business.exceptions

import de.partspicker.web.common.business.exceptions.RuleException

class RequiredItemTypeAmountSmallerThanAssignedException(requiredAmount: Long, assignedAmount: Long) :
    RuleException("The required amount $requiredAmount is smaller than the assigned amount $assignedAmount")
