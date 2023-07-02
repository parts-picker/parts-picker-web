package de.partspicker.web.test.generators.inventory

import de.partspicker.web.inventory.business.objects.AssignableItem
import de.partspicker.web.test.generators.ItemEntityGenerators
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.positiveLong
import io.kotest.property.arbitrary.string

class AssignableItemGenerators private constructor() {
    companion object {
        val generator: Arb<AssignableItem> = Arb.bind(
            ItemEntityGenerators.assignableGenerator,
            RequiredItemTypeGenerators.amountPairGenerator,
            Arb.positiveLong(),
            Arb.string(5..15)
        ) { itemEntity, amountPair, projectId, projectStatus ->
            AssignableItem.from(
                itemEntity,
                amountPair.first,
                amountPair.second,
                projectId,
                projectStatus
            )
        }
    }
}
