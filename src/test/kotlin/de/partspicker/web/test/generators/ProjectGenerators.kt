package de.partspicker.web.test.generators

import de.partspicker.web.project.business.objects.Project
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class ProjectGenerators private constructor() {

    companion object {
        val generator: Arb<Project> = Arb.bind(
            Arb.long(0),
            Arb.string(range = IntRange(3, 16)),
            Arb.descriptionLikeString(),
            Arb.descriptionLikeString(),
            GroupGenerators.generator,
            Arb.long(1),
            Arb.string(range = IntRange(3, 16)),
            Arb.boolean()
        ) { id, name, shortDescription, description, group, instanceId, status, active ->
            Project(
                id = id,
                name = name,
                shortDescription = shortDescription,
                description = description,
                group = group,
                workflowInstanceId = instanceId,
                status = status,
                active = active
            )
        }
    }
}
