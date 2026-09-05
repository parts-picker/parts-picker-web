package de.partspicker.web.test.generators

import de.partspicker.web.orgunit.persistence.entities.OrgUnitEntity
import de.partspicker.web.user.persistence.entities.UserEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string

class OrgUnitEntityGenerators private constructor() {

    companion object {
        val generator: Arb<OrgUnitEntity> = UserEntityGenerators.humanGenerator.flatMap { generatorFor(it) }

        /**
         * Returns an arbitrary generator that generates a random org unit owned by the given user.
         */
        fun generatorFor(owner: UserEntity): Arb<OrgUnitEntity> = arbitrary {
            OrgUnitEntity(
                id = Arb.long(0).bind(),
                name = Arb.string(range = IntRange(3, 16)).bind(),
                shortDescription = Arb.descriptionLikeString().orNull().bind(),
                owner = owner,
                creation = CreationInfoGenerators.generatorFor(owner).bind()
            )
        }
    }
}
