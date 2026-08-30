package de.partspicker.web.user.persistence

import de.partspicker.web.test.annotations.ReducedSpringTestContext
import de.partspicker.web.test.generators.UserEntityGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import org.springframework.dao.DataIntegrityViolationException

@ReducedSpringTestContext
class UserRepositoryIntTest(
    private val cut: UserRepository
) : ShouldSpec({

    // id 0 leaves the id to the sequence
    val newUser = UserEntityGenerators.humanGenerator.next().copy(id = 0)

    context("findByIssuerAndSubject") {
        should("return the user with the given issuer & subject when it exists") {
            // given
            val saved = cut.saveAndFlush(newUser.copy())

            // when
            val found = cut.findByIssuerAndSubject(saved.issuer, saved.subject)

            // then
            found shouldBe saved
        }

        should("return null when no user with the given issuer & subject exists") {
            // given
            val saved = cut.saveAndFlush(newUser.copy())

            // when
            val found = cut.findByIssuerAndSubject(saved.issuer, "unknown-subject")

            // then
            found.shouldBeNull()
        }

        should("distinguish users with the same subject but a different issuer") {
            // given
            val sharedSubject = "shared-subject"
            val first = cut.saveAndFlush(newUser.copy(issuer = "https://issuer-one", subject = sharedSubject))
            val second = cut.saveAndFlush(newUser.copy(issuer = "https://issuer-two", subject = sharedSubject))

            // when & then
            cut.findByIssuerAndSubject("https://issuer-one", sharedSubject) shouldBe first
            cut.findByIssuerAndSubject("https://issuer-two", sharedSubject) shouldBe second
        }
    }

    context("unique constraint on issuer & subject") {
        should("reject a second user with the same issuer & subject") {
            // given
            val existing = cut.saveAndFlush(newUser.copy())

            // when & then
            shouldThrow<DataIntegrityViolationException> {
                cut.saveAndFlush(newUser.copy(issuer = existing.issuer, subject = existing.subject))
            }
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
