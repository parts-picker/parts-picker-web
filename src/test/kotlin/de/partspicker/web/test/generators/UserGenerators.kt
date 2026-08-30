package de.partspicker.web.test.generators

import de.partspicker.web.user.business.objects.User
import de.partspicker.web.user.business.objects.UserIdentity
import de.partspicker.web.user.business.objects.enums.UserType
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.long

class UserGenerators private constructor() {

    companion object {
        val generator: Arb<User> = Arb.enum<UserType>().flatMap { type ->
            UserIdentityGenerators.generator.flatMap { generatorFor(it, type) }
        }

        val humanGenerator: Arb<User> = UserIdentityGenerators.generator.flatMap {
            generatorFor(it, UserType.HUMAN)
        }

        /**
         * A user carrying exactly the claims of the given identity.
         */
        fun generatorFor(
            userIdentity: UserIdentity,
            type: UserType = UserType.HUMAN
        ): Arb<User> = arbitrary {
            User(
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
