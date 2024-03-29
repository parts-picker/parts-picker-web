package de.partspicker.web.test.generators.inventory

import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.test.generators.ItemTypeGenerators
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.positiveLong
import io.kotest.property.arbitrary.string

class RequiredItemTypeGenerators private constructor() {
    companion object {
        /**
         * Generates a pair of longs. First for assigned and second  required amount
         */
        val amountPairGenerator: Arb<Pair<Long, Long>> = arbitrary {
                rs: RandomSource ->
            val required = rs.random.nextLong(1, Long.MAX_VALUE)
            val assigned = rs.random.nextLong(1, required + 1)

            Pair(assigned, required)
        }

        val generator: Arb<RequiredItemType> = Arb.bind(
            Arb.positiveLong(),
            Arb.string(5..10),
            ItemTypeGenerators.generator,
            amountPairGenerator
        ) { projectId, projectStatus, itemType, amountPair ->
            RequiredItemType(
                projectId = projectId,
                projectStatus = projectStatus,
                itemType = itemType,
                assignedAmount = amountPair.first,
                requiredAmount = amountPair.second
            )
        }

        val requiredEqualAssignedGenerator: Arb<RequiredItemType> = Arb.bind(
            Arb.positiveLong(),
            Arb.string(5..10),
            ItemTypeGenerators.generator,
            Arb.positiveLong()
        ) { projectId, projectStatus, itemType, amount ->
            RequiredItemType(
                projectId = projectId,
                projectStatus = projectStatus,
                itemType = itemType,
                assignedAmount = amount,
                requiredAmount = amount
            )
        }
    }
}
