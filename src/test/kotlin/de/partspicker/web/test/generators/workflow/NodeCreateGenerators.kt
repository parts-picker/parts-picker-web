package de.partspicker.web.test.generators.workflow

import de.partspicker.web.test.util.AutomatedTestAction
import de.partspicker.web.workflow.business.objects.create.enums.StartTypeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.AutomatedActionNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.NodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StartNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StopNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.UserActionNodeCreate
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.map

class NodeCreateGenerators private constructor() {
    companion object {

        val userActionNodeCreateGenerator: Arb<UserActionNodeCreate> =
            Arb.realisticNodeName().map { UserActionNodeCreate(it, it) }

        val automatedActionNodeCreateGenerator: Arb<AutomatedActionNodeCreate> = Arb.realisticNodeName().map {
            AutomatedActionNodeCreate(it, it, AutomatedTestAction.NAME)
        }

        val randomStartTypeGen = Arb.enum<StartTypeCreate>()

        val startNodeCreateGenerator: Arb<StartNodeCreate> = Arb.bind(
            Arb.realisticNodeName(),
            randomStartTypeGen
        ) { name, startType ->
            StartNodeCreate(name, name, startType)
        }

        val stopNodeCreateGenerator: Arb<StopNodeCreate> = Arb.realisticNodeName().map {
            StopNodeCreate(it, it)
        }

        val actionNodeGenerator = Arb.choose(
            7 to userActionNodeCreateGenerator,
            4 to automatedActionNodeCreateGenerator
        )

        val generator: Arb<NodeCreate> = Arb.choose(
            6 to userActionNodeCreateGenerator,
            4 to automatedActionNodeCreateGenerator,
            1 to startNodeCreateGenerator,
            1 to stopNodeCreateGenerator
        )
    }
}
