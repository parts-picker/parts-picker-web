package de.partspicker.web.test.generators.inventory

import de.partspicker.web.inventory.business.objects.enums.InventoryItemCondition
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum

class InventoryItemConditionGenerators private constructor() {
    companion object {
        val generator = Arb.enum<InventoryItemCondition>()
    }
}
