package de.partspicker.web.test.generators

import de.partspicker.web.project.persistance.entities.GroupEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class GroupEntityGenerators private constructor() {

    companion object {
        val generator: Arb<GroupEntity> = Arb.bind(
            Arb.long(0),
            Arb.string(range = IntRange(3, 16)),
            Arb.descriptionLikeString()
        ) { id, name, description ->
            GroupEntity(
                id = id,
                name = name,
                description = description
            )
        }
    }
}
