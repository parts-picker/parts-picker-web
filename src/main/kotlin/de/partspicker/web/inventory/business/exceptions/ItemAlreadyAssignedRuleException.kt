package de.partspicker.web.inventory.business.exceptions

import de.partspicker.web.common.business.exceptions.RuleException

class ItemAlreadyAssignedRuleException(itemId: Long) :
    RuleException("Item with id '$itemId' may not be assigned to another project while it is already assigned")
