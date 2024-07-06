package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.business.objects.enums.StartType
import de.partspicker.web.workflow.business.objects.nodes.AutomatedActionNode
import de.partspicker.web.workflow.business.objects.nodes.Node
import de.partspicker.web.workflow.business.objects.nodes.StartNode
import de.partspicker.web.workflow.business.objects.nodes.StopNode
import de.partspicker.web.workflow.business.objects.nodes.UserActionNode
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.positiveLong
import io.kotest.property.arbitrary.string

class NodeGenerators private constructor() {
    companion object {
        val userActionNodeGenerator: Arb<UserActionNode> = Arb.bind(
            Arb.positiveLong(),
            Arb.positiveLong(),
            Arb.realisticNodeName()
        ) { id, workflowId, name ->
            UserActionNode(
                id = id,
                workflowId = workflowId,
                name = name,
                displayName = name
            )
        }

        val automatedActionNodeGenerator: Arb<AutomatedActionNode> = Arb.bind(
            Arb.positiveLong(),
            Arb.positiveLong(),
            Arb.realisticNodeName(),
            Arb.string(range = 3..16)
        ) { id, workflowId, name, automatedActionName ->
            AutomatedActionNode(
                id = id,
                workflowId = workflowId,
                name = name,
                displayName = name,
                automatedActionName = automatedActionName
            )
        }

        val randomStartTypeGen = Arb.enum<StartType>()

        val startNodeGenerator: Arb<StartNode> = Arb.bind(
            Arb.positiveLong(),
            Arb.positiveLong(),
            Arb.realisticNodeName(),
            randomStartTypeGen
        ) { id, workflowId, name, startType ->
            StartNode(
                id = id,
                workflowId = workflowId,
                name = name,
                displayName = name,
                startType = startType
            )
        }

        val stopNodeGenerator: Arb<StopNode> = Arb.bind(
            Arb.positiveLong(),
            Arb.positiveLong(),
            Arb.realisticNodeName(),
        ) { id, workflowId, name ->
            StopNode(
                id = id,
                workflowId = workflowId,
                name = name,
                displayName = name
            )
        }

        val actionNodeGenerator = Arb.choose(
            7 to userActionNodeGenerator,
            4 to automatedActionNodeGenerator
        )

        val generator: Arb<Node> = Arb.choose(
            6 to userActionNodeGenerator,
            4 to automatedActionNodeGenerator,
            1 to startNodeGenerator,
            1 to stopNodeGenerator
        )
    }
}
