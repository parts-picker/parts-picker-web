package de.partspicker.web.test.generators

import de.partspicker.web.user.business.objects.UserIdentity
import de.partspicker.web.user.persistence.entities.UserEntity
import de.partspicker.web.user.persistence.entities.enums.UserTypeEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.long

class UserEntityGenerators private constructor() {

    companion object {
        val generator: Arb<UserEntity> = Arb.enum<UserTypeEntity>().flatMap { type ->
            UserIdentityGenerators.generator.flatMap { generatorFor(it, type) }
        }

        val humanGenerator: Arb<UserEntity> = UserIdentityGenerators.generator.flatMap {
            generatorFor(it, UserTypeEntity.HUMAN)
        }

        /**
         * A user having the same values as the given identity.
         */
        fun generatorFor(
            userIdentity: UserIdentity,
            type: UserTypeEntity = UserTypeEntity.HUMAN
        ): Arb<UserEntity> = arbitrary {
            UserEntity(
                id = Arb.long(0).bind(),
                issuer = userIdentity.issuer,
                subject = userIdentity.subject,
                username = userIdentity.username,
                displayName = userIdentity.displayName,
                type = type
            )
        }
    }
}
