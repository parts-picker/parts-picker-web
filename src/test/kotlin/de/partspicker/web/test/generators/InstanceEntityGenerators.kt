package de.partspicker.web.test.generators

import de.partspicker.web.workflow.persistance.entities.InstanceEntity
import de.partspicker.web.workflow.persistance.entities.InstanceValueEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.long

class InstanceEntityGenerators private constructor() {

    companion object {
        val generator: Arb<InstanceEntity> = Arb.bind(
            Arb.long(1),
            WorkflowEntityGenerators.generator,
            NodeEntityGenerators.generator,
            Arb.constant(emptySet<InstanceValueEntity>()),
            Arb.boolean()
        ) { id, workflow, currentNode, values, active ->
            InstanceEntity(
                id = id,
                workflow = workflow,
                currentNode = currentNode,
                values = values,
                active = active
            )
        }
    }
}
