package de.partspicker.web.inventory.business.exceptions

import de.partspicker.web.common.business.exceptions.RuleException
import de.partspicker.web.item.business.objects.enums.ItemStatus

class AssignableItemMustBeInStockRuleException(itemId: Long, itemStatus: ItemStatus) :
    RuleException("Status of item with id $itemId must be IN_STOCK to be assignable but was $itemStatus")
