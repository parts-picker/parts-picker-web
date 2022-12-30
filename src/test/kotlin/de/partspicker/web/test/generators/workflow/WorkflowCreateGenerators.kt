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
            Arb.list(NodeCreateGenerators.actionNodeGenerator, IntRange(4, 10)),
            NodeCreateGenerators.startNodeCreateGenerator,
            NodeCreateGenerators.stopNodeCreateGenerator
        ) { name, version, nodes, startNode, stopNode ->
            val nodeNames = nodes.map { it.name }
            val edges = EdgeCreateGenerators.generatorWithNodes(nodeNames, startNode.name, stopNode.name).values
            var nodesWithStartAndStop = nodes + startNode + stopNode

            WorkflowCreate(
                name = name,
                version = version,
                nodes = nodesWithStartAndStop,
                edges = edges
            )
        }
    }
}
