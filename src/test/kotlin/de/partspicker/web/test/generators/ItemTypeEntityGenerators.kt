package de.partspicker.web.test.generators

import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class ItemTypeEntityGenerators {
    private constructor()

    companion object {

        val generator: Arb<ItemTypeEntity> = Arb.bind(
            Arb.long(0),
            Arb.string(range = IntRange(3, 16)),
            Arb.descriptionLikeString()
        ) { id, name, description ->
            ItemTypeEntity(
                id = id,
                name = name,
                description = description
            )
        }
    }
}
