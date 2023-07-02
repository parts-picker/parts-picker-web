package de.partspicker.web.inventory.business.exceptions

import de.partspicker.web.common.business.exceptions.RuleException

class AssignedItemMissingProjectRuleException(itemId: Long) :
    RuleException("Item with id '$itemId' has no project assigned")
