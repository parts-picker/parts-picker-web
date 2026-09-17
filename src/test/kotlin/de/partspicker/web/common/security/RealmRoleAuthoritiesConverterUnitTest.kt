package de.partspicker.web.common.security

import de.partspicker.web.test.generators.JwtGenerators
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next

class RealmRoleAuthoritiesConverterUnitTest : ShouldSpec({
    val cut = RealmRoleAuthoritiesConverter()

    context("convert") {
        should("map every realm role to a prefixed authority") {
            // given
            val claims = mapOf(JwtClaims.REALM_ACCESS to mapOf(JwtClaims.ROLES to listOf("admin", "user")))
            val token = JwtGenerators.generatorFor(claims).next()

            // when
            val authorities = cut.convert(token)

            // then
            authorities.map { it.authority } shouldContainExactlyInAnyOrder listOf("ROLE_admin", "ROLE_user")
        }

        should("return no authorities when the token carries no realm access claim") {
            // given
            val token = JwtGenerators.generator.next()

            // when
            val authorities = cut.convert(token)

            // then
            authorities.shouldBeEmpty()
        }

        should("return no authorities when the realm access claim carries no roles") {
            // given
            val claims = mapOf(JwtClaims.REALM_ACCESS to mapOf("something-else" to "value"))
            val token = JwtGenerators.generatorFor(claims).next()

            // when
            val authorities = cut.convert(token)

            // then
            authorities.shouldBeEmpty()
        }

        should("return no authorities when the roles list is empty") {
            // given
            val claims = mapOf(JwtClaims.REALM_ACCESS to mapOf(JwtClaims.ROLES to emptyList<String>()))
            val token = JwtGenerators.generatorFor(claims).next()

            // when
            val authorities = cut.convert(token)

            // then
            authorities.shouldBeEmpty()
        }

        should("ignore blank & non string role entries") {
            // given
            val claims = mapOf(JwtClaims.REALM_ACCESS to mapOf(JwtClaims.ROLES to listOf("user", "  ", 42)))
            val token = JwtGenerators.generatorFor(claims).next()

            // when
            val authorities = cut.convert(token)

            // then
            authorities.map { it.authority } shouldBe listOf("ROLE_user")
        }
    }
})
