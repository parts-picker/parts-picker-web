package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import de.partspicker.web.workflow.persistence.entities.enums.DisplayTypeEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.long

class InstanceEntityGenerators private constructor() {

    companion object {
        val generator: Arb<InstanceEntity> = Arb.bind(
            Arb.long(1),
            NodeEntityGenerators.generator,
            Arb.boolean()
        ) { id, currentNode, active ->
            InstanceEntity(
                id = id,
                currentNode = currentNode,
                active = active,
                message = null,
                displayType = DisplayTypeEntity.DEFAULT
            )
        }
    }
}
