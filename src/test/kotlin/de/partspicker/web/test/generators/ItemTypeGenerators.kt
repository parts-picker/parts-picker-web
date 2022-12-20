package de.partspicker.web.test.generators

import de.partspicker.web.item.business.objects.ItemType
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class ItemTypeGenerators private constructor() {

    companion object {

        val generator: Arb<ItemType> = Arb.bind(
            Arb.long(0),
            Arb.string(range = IntRange(6, 20)),
            Arb.descriptionLikeString()
        ) { id, name, description ->
            ItemType(
                id = id,
                name = name,
                description = description
            )
        }
    }
}
