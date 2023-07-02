package de.partspicker.web.inventory.business.exceptions

import de.partspicker.web.common.business.exceptions.RuleException
import de.partspicker.web.item.business.objects.enums.ItemCondition

class InventoryItemMustBeUsableRuleException(itemId: Long, itemCondition: ItemCondition) :
    RuleException(
        "Item with id $itemId must be usable to be assignable/assigned, but its condition is $itemCondition"
    )
