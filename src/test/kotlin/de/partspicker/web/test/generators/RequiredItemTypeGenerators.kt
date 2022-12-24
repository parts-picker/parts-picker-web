package de.partspicker.web.test.generators

import de.partspicker.web.inventory.business.objects.RequiredItemType
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.long

class RequiredItemTypeGenerators private constructor() {
    companion object {
        val generator: Arb<RequiredItemType> = Arb.bind(
            Arb.long(min = 1),
            ItemTypeGenerators.generator,
            Arb.long(min = 1)
        ) { projectId, itemType, requiredAmount ->
            RequiredItemType(
                projectId = projectId,
                itemType = itemType,
                requiredAmount = requiredAmount
            )
        }
    }
}
