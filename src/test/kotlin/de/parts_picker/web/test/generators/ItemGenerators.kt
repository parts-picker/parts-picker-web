package de.parts_picker.web.test.generators

import de.parts_picker.web.item.business.objects.Item
import de.parts_picker.web.item.business.objects.enums.ItemCondition
import de.parts_picker.web.item.business.objects.enums.ItemStatus
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class ItemGenerators {
    companion object {
        val randomStatusGen = Arb.enum<ItemStatus>()

        val randomConditionGen = Arb.enum<ItemCondition>()

        val randomGen: Arb<Item> = Arb.bind(
            Arb.long(0),
            randomStatusGen,
            randomConditionGen,
            Arb.string()
        ) { id, status, condition, note ->
            Item(
                id = id,
                status = status,
                condition = condition,
                note = note
            )
        }
    }
}