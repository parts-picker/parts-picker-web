package de.partspicker.web.test.generators.inventory

import de.partspicker.web.inventory.business.objects.CreateOrUpdateRequiredItemType
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.positiveLong

class CreateOrUpdateRequiredItemTypeGenerators private constructor() {
    companion object {
        val generator: Arb<CreateOrUpdateRequiredItemType> = Arb.bind(
            Arb.positiveLong(),
            Arb.positiveLong(),
            Arb.positiveLong(),
        ) { projectId, itemTypeId, requiredAmount ->
            CreateOrUpdateRequiredItemType(
                projectId = projectId,
                itemTypeId = itemTypeId,
                requiredAmount = requiredAmount
            )
        }
    }
}
