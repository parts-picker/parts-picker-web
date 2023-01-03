package de.partspicker.web.test.generators

import de.partspicker.web.workflow.persistance.entities.InstanceEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.long

class InstanceEntityGenerators private constructor() {

    companion object {
        val generator: Arb<InstanceEntity> = Arb.bind(
            Arb.long(1),
            WorkflowEntityGenerators.generator,
            NodeEntityGenerators.generator,
            Arb.boolean()
        ) { id, workflow, currentNode, active ->
            InstanceEntity(
                id = id,
                workflow = workflow,
                currentNode = currentNode,
                active = active
            )
        }
    }
}
