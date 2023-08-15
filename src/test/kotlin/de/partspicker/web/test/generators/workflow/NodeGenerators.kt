package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.business.objects.enums.StartType
import de.partspicker.web.workflow.business.objects.nodes.Node
import de.partspicker.web.workflow.business.objects.nodes.StartNode
import de.partspicker.web.workflow.business.objects.nodes.StopNode
import de.partspicker.web.workflow.business.objects.nodes.UserActionNode
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.positiveLong
import io.kotest.property.arbitrary.string

class NodeGenerators private constructor() {
    companion object {
        private const val MIN_NAME_LENGTH = 10

        val userActionNodeGenerator: Arb<UserActionNode> = Arb.bind(
            Arb.positiveLong(),
            Arb.positiveLong(),
            Arb.string(range = MIN_NAME_LENGTH..16),
            Arb.string(range = 3..16),
        ) { id, workflowId, name, displayName ->
            UserActionNode(
                id = id,
                workflowId = workflowId,
                name = name,
                displayName = displayName
            )
        }

        val randomStartTypeGen = Arb.enum<StartType>()

        val startNodeGenerator: Arb<StartNode> = Arb.bind(
            Arb.positiveLong(),
            Arb.positiveLong(),
            Arb.string(range = MIN_NAME_LENGTH..16),
            Arb.string(range = 3..16),
            randomStartTypeGen
        ) { id, workflowId, name, displayName, startType ->
            StartNode(
                id = id,
                workflowId = workflowId,
                name = name,
                displayName = displayName,
                startType = startType
            )
        }

        val stopNodeGenerator: Arb<StopNode> = Arb.bind(
            Arb.positiveLong(),
            Arb.positiveLong(),
            Arb.string(range = MIN_NAME_LENGTH..16),
            Arb.string(range = 3..16),
        ) { id, workflowId, name, displayName ->
            StopNode(
                id = id,
                workflowId = workflowId,
                name = name,
                displayName = displayName
            )
        }

        val actionNodeGenerator = Arb.choice(userActionNodeGenerator)

        val generator: Arb<Node> = Arb.choose(
            8 to userActionNodeGenerator,
            1 to startNodeGenerator,
            1 to stopNodeGenerator
        )
    }
}
