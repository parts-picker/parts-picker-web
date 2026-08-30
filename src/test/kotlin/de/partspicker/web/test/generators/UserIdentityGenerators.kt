package de.partspicker.web.test.generators

import de.partspicker.web.user.business.objects.UserIdentity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uuid

class UserIdentityGenerators private constructor() {

    companion object {
        val generator: Arb<UserIdentity> = Arb.bind(
            Arb.issuer(),
            Arb.uuid(),
            Arb.string(range = IntRange(3, 16)),
            Arb.string(range = IntRange(3, 16))
        ) { issuer, subject, username, displayName ->
            UserIdentity(
                issuer = issuer,
                subject = subject.toString(),
                username = username,
                displayName = displayName
            )
        }
    }
}
