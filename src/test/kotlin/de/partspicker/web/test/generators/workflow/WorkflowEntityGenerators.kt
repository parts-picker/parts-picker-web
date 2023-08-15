package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.persistence.entities.WorkflowEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class WorkflowEntityGenerators private constructor() {

    companion object {
        val generator: Arb<WorkflowEntity> = Arb.bind(
            Arb.long(1),
            Arb.string(range = IntRange(3, 16)),
            Arb.long(1)
        ) { id, name, version ->
            WorkflowEntity(
                id = id,
                name = name,
                version = version
            )
        }
    }
}
