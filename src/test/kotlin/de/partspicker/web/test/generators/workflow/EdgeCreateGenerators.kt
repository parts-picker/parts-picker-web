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
         * @return Returns an exhaustive generator based on list of edges with the given nodeNames.
         */
        fun generatorWithNodeNames(nodeNames: List<String>): Exhaustive<EdgeCreate> {
            val nameArb = Arb.string(range = IntRange(3, 16))
            val conditionsArb = Arb.list(Arb.string(10), IntRange(0, 5))

            val resultList = mutableListOf<EdgeCreate>()
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

            return resultList.exhaustive()
        }
    }
}
