package de.partspicker.web.test.generators

import de.partspicker.web.item.persistance.entities.ItemEntity
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity.IN_STOCK
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity.RESERVED
import de.partspicker.web.project.persistance.entities.ProjectEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.positiveLong

class ItemEntityGenerators private constructor() {

    companion object {
        val randomStatusGen = Arb.enum<ItemStatusEntity>()

        val assignableStatusGen = Arb.element(IN_STOCK)

        val assignedStatusGen = Arb.element(RESERVED)

        val randomConditionGen = Arb.enum<ItemConditionEntity>()

        val usableConditionGen = Arb.element(
            ItemConditionEntity.NEW,
            ItemConditionEntity.USED,
            ItemConditionEntity.WRAPPED
        )

        val generator: Arb<ItemEntity> = createGenerator(GenerationType.ANY)

        val assignableGenerator: Arb<ItemEntity> = createGenerator(GenerationType.ASSIGNABLE)

        val assignedGenerator: Arb<ItemEntity> = createGenerator(GenerationType.ASSIGNED)

        private fun createGenerator(
            generationType: GenerationType
        ): Arb<ItemEntity> {
            return arbitrary {
                // static gens
                val id = Arb.positiveLong().bind()
                val itemType = ItemTypeEntityGenerators.generator.bind()
                val note = Arb.descriptionLikeString().bind()

                // dynamic gens
                val project: ProjectEntity?
                val condition: ItemConditionEntity
                val status: ItemStatusEntity
                when (generationType) {
                    GenerationType.ASSIGNABLE -> {
                        project = null
                        condition = usableConditionGen.bind()
                        status = assignableStatusGen.bind()
                    }
                    GenerationType.ASSIGNED -> {
                        project = ProjectEntityGenerators.generator.bind()
                        condition = usableConditionGen.bind()
                        status = assignedStatusGen.bind()
                    }
                    GenerationType.ANY -> {
                        project = ProjectEntityGenerators.generator.orNull(0.25).bind()
                        condition = if (project != null) {
                            usableConditionGen.bind()
                        } else {
                            randomConditionGen.bind()
                        }
                        status = if (project != null) {
                            assignedStatusGen.bind()
                        } else {
                            randomStatusGen.filter { it == RESERVED }.bind()
                        }
                    }
                }

                ItemEntity(
                    id = id,
                    assignedProject = project,
                    type = itemType,
                    condition = condition,
                    status = status,
                    note = note
                )
            }
        }
    }

    enum class GenerationType {
        ASSIGNABLE,
        ASSIGNED,
        ANY
    }
}
