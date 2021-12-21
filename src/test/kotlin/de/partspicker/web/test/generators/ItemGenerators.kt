package de.partspicker.web.test.generators

import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class ItemGenerators private constructor() {

    companion object {
        val randomStatusGen = Arb.enum<ItemStatus>()

        val randomConditionGen = Arb.enum<ItemCondition>()

        val randomGen: Arb<Item> = Arb.bind(
            Arb.long(0),
            ItemTypeGenerators.generator,
            randomStatusGen,
            randomConditionGen,
            Arb.string()
        ) { id, type, status, condition, note ->
            Item(
                id = id,
                type = type,
                status = status,
                condition = condition,
                note = note
            )
        }
    }
}
