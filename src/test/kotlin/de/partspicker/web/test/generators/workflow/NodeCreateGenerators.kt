package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.business.objects.create.nodes.NodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.UserActionNodeCreate
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.string

class NodeCreateGenerators private constructor() {
    companion object {
        val userActionNodeCreateGenerator: Arb<UserActionNodeCreate> = Arb.bind(
            Arb.string(range = IntRange(3, 16)),
            Arb.string(range = IntRange(3, 16))
        ) { name, displayName ->
            UserActionNodeCreate(name, displayName)
        }

        val generator: Arb<NodeCreate> = Arb.choice(userActionNodeCreateGenerator)
    }
}
