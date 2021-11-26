package de.parts_picker.web.test.generators

import de.parts_picker.web.item.persistance.entities.ItemEntity
import de.parts_picker.web.item.persistance.entities.enums.ItemConditionEntity
import de.parts_picker.web.item.persistance.entities.enums.ItemStatusEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*

class ItemEntityGenerators {
    companion object {
        val randomStatusGen = Arb.enum<ItemStatusEntity>()

        val randomConditionGen = Arb.enum<ItemConditionEntity>()

        val generator: Arb<ItemEntity> = Arb.bind(
            Arb.long(0),
            ItemTypeEntityGenerators.generator,
            randomStatusGen,
            randomConditionGen,
            Arb.descriptionLikeString()
        ) { id, type, status, condition, note ->
            ItemEntity(
                id = id,
                type = type,
                status = status,
                condition = condition,
                note = note
            )
        }
    }
}