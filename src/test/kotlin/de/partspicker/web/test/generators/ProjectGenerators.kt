package de.partspicker.web.test.generators

import de.partspicker.web.project.business.objects.Project
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class ProjectGenerators private constructor() {

    companion object {
        val generator: Arb<Project> = Arb.bind(
            Arb.long(0),
            Arb.string(range = IntRange(3, 16)),
            Arb.descriptionLikeString(),
            GroupGenerators.generator
        ) { id, name, description, group ->
            Project(
                id = id,
                name = name,
                description = description,
                group = group
            )
        }
    }
}
