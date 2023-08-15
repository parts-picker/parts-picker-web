package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.persistence.entities.EdgeEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class EdgeEntityGenerators private constructor() {
    companion object {
        val generator: Arb<EdgeEntity> = Arb.bind(
            Arb.long(1),
            WorkflowEntityGenerators.generator,
            Arb.string(range = IntRange(3, 16)),
            Arb.string(range = IntRange(3, 16)),
            NodeEntityGenerators.generator,
            NodeEntityGenerators.generator,
            Arb.list(Arb.string(10), IntRange(0, 5))
        ) { id, workflow, name, displayName, source, target, conditionKeys ->
            EdgeEntity(
                id = id,
                workflow = workflow,
                name = name,
                displayName = displayName,
                source = source,
                target = target,
                conditionKeys = conditionKeys
            )
        }
    }
}
