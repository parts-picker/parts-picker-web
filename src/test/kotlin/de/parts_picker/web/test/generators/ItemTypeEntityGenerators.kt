package de.parts_picker.web.test.generators

import de.parts_picker.web.item.persistance.entities.ItemTypeEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*

class ItemTypeEntityGenerators {
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

fun main(args: Array<String>) {
    repeat(50) {
        print("val: ")
        println(ItemTypeEntityGenerators.generator.next())
    }

}