package de.partspicker.web.test.generators

import de.partspicker.web.item.persistance.entities.ItemEntity
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.long

class ItemEntityGenerators {
    private constructor()

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
