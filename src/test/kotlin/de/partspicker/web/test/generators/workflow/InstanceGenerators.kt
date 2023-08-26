package de.partspicker.web.test.generators.workflow

import de.partspicker.web.test.generators.id
import de.partspicker.web.workflow.business.objects.Instance
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean

class InstanceGenerators private constructor() {

    companion object {
        val generator: Arb<Instance> = Arb.bind(
            Arb.id(),
            NodeGenerators.generator,
            Arb.boolean(),
            Arb.id()
        ) { id, currentNode, active, workflowId ->
            Instance(
                id = id,
                currentNode = currentNode,
                active = active,
                workflowId = workflowId
            )
        }
    }
}
