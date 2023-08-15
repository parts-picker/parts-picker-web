package de.partspicker.web.test.generators.workflow

import de.partspicker.web.test.generators.id
import de.partspicker.web.workflow.business.objects.Workflow
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class WorkflowGenerators private constructor() {
    companion object {
        val generator: Arb<Workflow> = Arb.bind(
            Arb.id(),
            Arb.string(range = IntRange(3, 16)),
            Arb.long(1),
            Arb.list(NodeGenerators.actionNodeGenerator, 4..10),
            NodeGenerators.startNodeGenerator,
            NodeGenerators.stopNodeGenerator
        ) { id, name, version, nodes, startNode, stopNode ->
            val nodeIds = nodes.map { it.id }
            val edges = EdgeGenerators.generatorWithNodes(nodeIds, startNode.id, stopNode.id).values
            val nodesWithStartAndStop = nodes + startNode + stopNode

            Workflow(
                id = id,
                name = name,
                version = version,
                nodes = nodesWithStartAndStop,
                edges = edges
            )
        }
    }
}
