package de.partspicker.web.test.generators

import de.partspicker.web.workflow.persistance.entities.EdgeEntity
import de.partspicker.web.workflow.persistance.entities.WorkflowEntity
import de.partspicker.web.workflow.persistance.entities.nodes.NodeEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import java.time.OffsetDateTime

class WorkflowEntityGenerators private constructor() {

    companion object {
        val generator: Arb<WorkflowEntity> = Arb.bind(
            Arb.long(1),
            Arb.string(range = IntRange(3, 16)),
            Arb.long(1),
            Arb.constant(emptySet<NodeEntity>()),
            Arb.constant(emptySet<EdgeEntity>()),
            Arb.constant(OffsetDateTime.now())

        ) { id, name, version, nodes, edges, createdOn ->
            WorkflowEntity(
                id = id,
                name = name,
                version = version,
                nodes = nodes,
                edges = edges,
                createdOn = createdOn
            )
        }
    }
}
