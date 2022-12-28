package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.business.objects.create.WorkflowCreate
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class WorkflowCreateGenerators private constructor() {
    companion object {
        val generator: Arb<WorkflowCreate> = Arb.bind(
            Arb.string(range = IntRange(3, 16)),
            Arb.long(1, 1000),
            Arb.list(NodeCreateGenerators.generator, IntRange(4, 10))
        ) { name, version, nodes ->
            val nodeNames = nodes.map { it.name }
            val edges = EdgeCreateGenerators.generatorWithNodeNames(nodeNames).values

            WorkflowCreate(
                name = name,
                version = version,
                nodes = nodes,
                edges = edges
            )
        }
    }
}
