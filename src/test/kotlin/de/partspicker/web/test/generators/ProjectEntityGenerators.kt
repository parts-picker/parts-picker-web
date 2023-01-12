package de.partspicker.web.test.generators

import de.partspicker.web.project.persistance.entities.ProjectEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class ProjectEntityGenerators private constructor() {

    companion object {
        val generator: Arb<ProjectEntity> = Arb.bind(
            Arb.long(0),
            Arb.string(range = IntRange(3, 16)),
            Arb.descriptionLikeString(),
            GroupEntityGenerators.generator,
            InstanceEntityGenerators.generator
        ) { id, name, description, group, instanceId ->
            ProjectEntity(
                id = id,
                name = name,
                description = description,
                group = group,
                workflowInstance = instanceId
            )
        }
    }
}
