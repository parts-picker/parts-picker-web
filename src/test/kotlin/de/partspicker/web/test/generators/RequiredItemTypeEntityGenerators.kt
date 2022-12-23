package de.partspicker.web.test.generators

import de.partspicker.web.inventory.persitance.embeddableIds.RequiredItemTypeId
import de.partspicker.web.inventory.persitance.entities.RequiredItemTypeEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.long

class RequiredItemTypeEntityGenerators private constructor() {

    companion object {
        val generator: Arb<RequiredItemTypeEntity> = Arb.bind(
            ProjectEntityGenerators.generator,
            ItemTypeEntityGenerators.generator,
            Arb.long(min = 1)
        ) { project, itemType, requiredAmount ->
            RequiredItemTypeEntity(
                RequiredItemTypeId(projectId = project.id, itemTypeId = itemType.id),
                project,
                itemType,
                requiredAmount
            )
        }
    }
}
