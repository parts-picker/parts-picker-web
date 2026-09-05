package de.partspicker.web.test.util

import de.partspicker.web.test.generators.JwtGenerators
import de.partspicker.web.user.business.objects.UserIdentity
import io.kotest.property.arbitrary.next
import org.springframework.security.oauth2.jwt.Jwt

/**
 * The user every integration test acts as. The ids match init-sql/testUser.sql.
 */
object TestUsers {
    const val ID = 1000L
    const val ORG_UNIT_ID = 1000L

    val IDENTITY = UserIdentity(
        issuer = "urn:parts-picker:test",
        subject = "test-subject",
        username = "testuser",
        displayName = "Test User"
    )

    fun jwt(): Jwt = JwtGenerators.generatorFor(IDENTITY).next()
}

/**
 * The org unit of init-sql/unrelatedOrgUnit.sql, which [TestUsers] is no member of.
 */
object UnrelatedOrgUnit {
    const val ID = 2000L
    const val ITEM_TYPE_ID = 2000L
    const val ITEM_ID = 2000L
    const val GROUP_ID = 2000L
}
