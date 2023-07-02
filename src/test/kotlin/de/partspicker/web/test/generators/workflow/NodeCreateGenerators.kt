package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.business.objects.create.enums.StartTypeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.NodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StartNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StopNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.UserActionNodeCreate
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.string

class NodeCreateGenerators private constructor() {
    companion object {
        private const val MIN_NAME_LENGTH = 10

        val userActionNodeCreateGenerator: Arb<UserActionNodeCreate> = Arb.bind(
            Arb.string(range = IntRange(MIN_NAME_LENGTH, 16)),
            Arb.string(range = IntRange(3, 16))
        ) { name, displayName ->
            UserActionNodeCreate(name, displayName)
        }

        val randomStartTypeGen = Arb.enum<StartTypeCreate>()

        val startNodeCreateGenerator: Arb<StartNodeCreate> = Arb.bind(
            Arb.string(range = IntRange(MIN_NAME_LENGTH, 16)),
            Arb.string(range = IntRange(3, 16)),
            randomStartTypeGen
        ) { name, displayName, startType ->
            StartNodeCreate(name, displayName, startType)
        }

        val stopNodeCreateGenerator: Arb<StopNodeCreate> = Arb.bind(
            Arb.string(range = IntRange(MIN_NAME_LENGTH, 16)),
            Arb.string(range = IntRange(3, 16))
        ) { name, displayName ->
            StopNodeCreate(name, displayName)
        }

        val actionNodeGenerator = Arb.choice(userActionNodeCreateGenerator)

        val generator: Arb<NodeCreate> = Arb.choose(
            8 to userActionNodeCreateGenerator,
            1 to startNodeCreateGenerator,
            1 to stopNodeCreateGenerator
        )
    }
}
