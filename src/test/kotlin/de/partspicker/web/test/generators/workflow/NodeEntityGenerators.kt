package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.persistence.entities.nodes.AutomatedActionNodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.UserActionNodeEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class NodeEntityGenerators private constructor() {
    companion object {
        val userActionNodeEntityGenerator: Arb<UserActionNodeEntity> = Arb.bind(
            Arb.long(1),
            WorkflowEntityGenerators.generator,
            Arb.string(range = IntRange(3, 16)),
            Arb.string(range = IntRange(3, 16))
        ) { id, workflow, name, displayName ->
            UserActionNodeEntity(
                id = id,
                workflow = workflow,
                name = name,
                displayName = displayName
            )
        }

        val automatedActionNodeEntityGenerator: Arb<AutomatedActionNodeEntity> = Arb.bind(
            Arb.long(1),
            WorkflowEntityGenerators.generator,
            Arb.string(range = IntRange(3, 16)),
            Arb.string(range = IntRange(3, 16)),
            Arb.string(range = IntRange(3, 16))
        ) { id, workflow, name, displayName, automatedActionName ->
            AutomatedActionNodeEntity(
                id = id,
                workflow = workflow,
                name = name,
                displayName = displayName,
                automatedActionName = automatedActionName
            )
        }

        // add new child class generators here
        val generator: Arb<NodeEntity> = Arb.choice(userActionNodeEntityGenerator, automatedActionNodeEntityGenerator)
    }
}
