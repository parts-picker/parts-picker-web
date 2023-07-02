package de.partspicker.web.test.generators.inventory

import de.partspicker.web.inventory.business.objects.AssignedItem
import de.partspicker.web.test.generators.ItemEntityGenerators
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map

class AssignedItemGenerators private constructor() {
    companion object {
        val generator: Arb<AssignedItem> = ItemEntityGenerators.assignedGenerator.map {
            AssignedItem.from(it)
        }
    }
}
