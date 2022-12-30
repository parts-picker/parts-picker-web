package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.business.objects.create.EdgeCreate
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.kotest.property.exhaustive.exhaustive

class EdgeCreateGenerators private constructor() {
    companion object {
        /**
         * Generator for EdgeCreate objects.
         * The edges will start with a random node and all will be connected like a linked list.
         * No branches will be generated.
         *
         * @param [nodeNames] A list of nodes, which will be used as source/target nodes for the edges.
         * @param [startNodeName] The name which will be used as name for the start node.
         * @param [stopNodeName] The name which will be used as name for the stop node.
         * @return Returns an exhaustive generator based on list of edges with the given nodeNames.
         */
        fun generatorWithNodes(
            nodeNames: List<String>,
            startNodeName: String,
            stopNodeName: String
        ): Exhaustive<EdgeCreate> {
            val nameArb = Arb.string(range = IntRange(3, 16))
            val conditionsArb = Arb.list(Arb.string(10), IntRange(0, 5))

            val resultList = mutableListOf<EdgeCreate>()

            // add startNode -> first node
            resultList.add(
                EdgeCreate(
                    nameArb.next(),
                    nameArb.next(),
                    startNodeName,
                    nodeNames[0],
                    conditionsArb.next()
                )
            )

            for (i in 0..nodeNames.size - 2)
                resultList.add(
                    EdgeCreate(
                        nameArb.next(),
                        nameArb.next(),
                        nodeNames[i],
                        nodeNames[i + 1],
                        conditionsArb.next()
                    )
                )

            // add last node -> stopNode
            resultList.add(
                EdgeCreate(
                    nameArb.next(),
                    nameArb.next(),
                    nodeNames[nodeNames.size - 1],
                    stopNodeName,
                    conditionsArb.next()
                )
            )

            return resultList.exhaustive()
        }
    }
}
