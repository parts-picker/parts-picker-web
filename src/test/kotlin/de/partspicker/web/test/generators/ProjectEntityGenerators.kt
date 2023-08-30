package de.partspicker.web.test.generators

import de.partspicker.web.project.persistance.entities.ProjectEntity
import de.partspicker.web.test.generators.workflow.InstanceEntityGenerators
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.positiveLong
import io.kotest.property.arbitrary.string

class ProjectEntityGenerators private constructor() {

    companion object {
        val generator: Arb<ProjectEntity> = Arb.bind(
            Arb.positiveLong(),
            Arb.string(range = IntRange(3, 16)),
            Arb.descriptionLikeString(),
            Arb.descriptionLikeString(),
            GroupEntityGenerators.generator,
            InstanceEntityGenerators.generator
        ) { id, name, shortDescription, description, group, instanceId ->
            ProjectEntity(
                id = id,
                name = name,
                shortDescription = shortDescription,
                description = description,
                group = group,
                workflowInstance = instanceId
            )
        }
    }
}
