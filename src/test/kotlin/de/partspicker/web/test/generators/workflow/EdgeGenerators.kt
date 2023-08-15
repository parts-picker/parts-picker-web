package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.business.objects.Edge
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.positiveLong
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.exhaustive.exhaustive

class EdgeGenerators private constructor() {
    companion object {
        /**
         * Generator for Edge objects.
         * The edges will start with a random node and all will be connected like a linked list.
         * No branches will be generated.
         *
         * @param [nodeIds] A list of node ids, which will be used as source/target nodes for the edges.
         * @param [startNodeId] The value which will be used as id for the start node.
         * @param [stopNodeId] The value which will be used as id for the stop node.
         * @return Returns an exhaustive generator based on list of edges with the given nodeNames.
         */

        fun generatorWithNodes(
            nodeIds: List<Long>,
            startNodeId: Long,
            stopNodeId: Long
        ): Exhaustive<Edge> {
            val idArb = Arb.positiveLong()
            val nameArb = Arb.string(range = 3..16)

            val resultList = mutableListOf<Edge>()

            // add startNode -> first node
            resultList.add(
                Edge(
                    id = idArb.single(),
                    workflowId = idArb.single(),
                    sourceNodeId = startNodeId,
                    targetNodeId = nodeIds[0],
                    name = nameArb.single(),
                    displayName = nameArb.single()
                )
            )

            for (i in 0..nodeIds.size - 2)
                resultList.add(
                    Edge(
                        id = idArb.single(),
                        workflowId = idArb.single(),
                        sourceNodeId = nodeIds[i],
                        targetNodeId = nodeIds[i + 1],
                        name = nameArb.single(),
                        displayName = nameArb.single()
                    )
                )

            // add last node -> stopNode
            resultList.add(
                Edge(
                    id = idArb.single(),
                    workflowId = idArb.single(),
                    sourceNodeId = nodeIds[nodeIds.size - 1],
                    targetNodeId = stopNodeId,
                    name = nameArb.single(),
                    displayName = nameArb.single()
                )
            )

            return resultList.exhaustive()
        }
    }
}
