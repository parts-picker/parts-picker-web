package de.partspicker.web.test.generators

import de.partspicker.web.common.security.JwtClaims
import de.partspicker.web.user.business.objects.UserIdentity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.string
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

class JwtGenerators private constructor() {

    companion object {
        private const val EXPIRES_IN_SECONDS = 60L

        val generator: Arb<Jwt> = UserIdentityGenerators.generator.flatMap { generatorFor(it) }

        /**
         * The claims keycloak issues for the given identity. Add to or subtract from it to build a token that
         * carries more or less than the usual.
         */
        fun claimsOf(userIdentity: UserIdentity): Map<String, Any> = buildMap {
            put(JwtClaims.ISSUER, userIdentity.issuer)
            put(JwtClaims.SUBJECT, userIdentity.subject)
            put(JwtClaims.PREFERRED_USERNAME, userIdentity.username)
            userIdentity.displayName?.let { put(JwtClaims.NAME, it) }
        }

        fun generatorFor(userIdentity: UserIdentity): Arb<Jwt> = generatorFor(claimsOf(userIdentity))

        fun generatorFor(claims: Map<String, Any>): Arb<Jwt> = arbitrary {
            val builder = Jwt.withTokenValue(Arb.string(range = IntRange(8, 24)).bind())
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRES_IN_SECONDS))

            claims.forEach { (name, value) -> builder.claim(name, value) }

            builder.build()
        }
    }
}
