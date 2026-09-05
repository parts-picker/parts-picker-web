package de.partspicker.web.common.security

import de.partspicker.web.common.security.exceptions.MissingClaimException
import de.partspicker.web.common.security.exceptions.NoAuthenticatedUserException
import de.partspicker.web.test.generators.JwtGenerators
import de.partspicker.web.test.generators.UserGenerators
import de.partspicker.web.test.generators.UserIdentityGenerators
import de.partspicker.web.user.business.UserService
import de.partspicker.web.user.business.exceptions.UserAlreadyProvisionedException
import de.partspicker.web.user.business.objects.UserIdentity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class CurrentUserProviderUnitTest :
    ShouldSpec({
        val userServiceMock = mockk<UserService>()

        afterTest {
            SecurityContextHolder.clearContext()
            clearMocks(userServiceMock)
        }

        context("getCurrentUser") {
            should("resolve the user from the claims of the given token") {
                // given
                val identity = UserIdentityGenerators.generator.next()
                val user = UserGenerators.humanGenerator.next()
                SecurityContextHolder.getContext().authentication =
                    JwtAuthenticationToken(JwtGenerators.generatorFor(identity).next(), emptyList())
                val identitySlot = slot<UserIdentity>()
                every { userServiceMock.resolve(capture(identitySlot)) } returns user

                // when
                val returnedUser = CurrentUserProvider(userServiceMock).getCurrentUser()

                // then
                returnedUser shouldBe user
                identitySlot.captured shouldBe identity
            }

            should("accept a token of a user without a display name") {
                // given
                val identity = UserIdentityGenerators.generator.next().copy(displayName = null)
                SecurityContextHolder.getContext().authentication =
                    JwtAuthenticationToken(JwtGenerators.generatorFor(identity).next(), emptyList())
                val identitySlot = slot<UserIdentity>()
                every { userServiceMock.resolve(capture(identitySlot)) } returns UserGenerators.humanGenerator.next()

                // when
                CurrentUserProvider(userServiceMock).getCurrentUser()

                // then
                identitySlot.captured.displayName shouldBe null
            }

            should("resolve only once per request when called repeatedly") {
                // given
                SecurityContextHolder.getContext().authentication =
                    JwtAuthenticationToken(JwtGenerators.generator.next(), emptyList())
                every { userServiceMock.resolve(any()) } returns UserGenerators.humanGenerator.next()
                val cut = CurrentUserProvider(userServiceMock)

                // when
                repeat(5) {
                    cut.getCurrentUser()
                }

                // then
                verify(exactly = 1) { userServiceMock.resolve(any()) }
            }

            should("resolve once more when the first attempt failed because of a racing condition") {
                // given
                val identity = UserIdentityGenerators.generator.next()
                val user = UserGenerators.humanGenerator.next()
                SecurityContextHolder.getContext().authentication =
                    JwtAuthenticationToken(JwtGenerators.generatorFor(identity).next(), emptyList())
                every { userServiceMock.resolve(any()) } throws
                    UserAlreadyProvisionedException(identity, DataIntegrityViolationException("uq")) andThen user

                // when
                val returnedUser = CurrentUserProvider(userServiceMock).getCurrentUser()

                // then
                returnedUser shouldBe user
                verify(exactly = 2) { userServiceMock.resolve(any()) }
            }

            should("throw NoAuthenticatedUserException when the context holds no authentication") {
                // given
                SecurityContextHolder.clearContext()

                // when & then
                shouldThrow<NoAuthenticatedUserException> {
                    CurrentUserProvider(userServiceMock).getCurrentUser()
                }
            }

            should("throw NoAuthenticatedUserException when the authentication is not token based") {
                // given
                SecurityContextHolder.getContext().authentication =
                    TestingAuthenticationToken("principal", "credentials")

                // when & then
                shouldThrow<NoAuthenticatedUserException> {
                    CurrentUserProvider(userServiceMock).getCurrentUser()
                }
            }

            should("throw MissingClaimException when the token carries no preferred username") {
                // given
                val identity = UserIdentityGenerators.generator.next()
                val claims = JwtGenerators.claimsOf(identity) - JwtClaims.PREFERRED_USERNAME
                SecurityContextHolder.getContext().authentication =
                    JwtAuthenticationToken(JwtGenerators.generatorFor(claims).next(), emptyList())

                // when & then
                shouldThrow<MissingClaimException> {
                    CurrentUserProvider(userServiceMock).getCurrentUser()
                }
            }
        }
    })
