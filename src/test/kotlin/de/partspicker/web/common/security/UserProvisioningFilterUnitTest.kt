package de.partspicker.web.common.security

import de.partspicker.web.common.security.exceptions.MissingClaimException
import de.partspicker.web.test.generators.JwtGenerators
import de.partspicker.web.test.generators.UserGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class UserProvisioningFilterUnitTest : ShouldSpec({
    val currentUserProviderMock = mockk<CurrentUserProvider>()
    val filterChainMock = mockk<FilterChain>()
    val cut = UserProvisioningFilter(currentUserProviderMock)

    beforeTest {
        every { filterChainMock.doFilter(any(), any()) } just runs
    }

    afterTest {
        SecurityContextHolder.clearContext()
        clearMocks(currentUserProviderMock, filterChainMock)
    }

    context("doFilter") {
        should("resolve the current user when the request is authenticated with a token") {
            // given
            SecurityContextHolder.getContext().authentication =
                JwtAuthenticationToken(JwtGenerators.generator.next(), emptyList())
            every { currentUserProviderMock.getCurrentUser() } returns UserGenerators.humanGenerator.next()

            // when
            cut.doFilter(MockHttpServletRequest(), MockHttpServletResponse(), filterChainMock)

            // then
            verify(exactly = 1) { currentUserProviderMock.getCurrentUser() }
            verify(exactly = 1) { filterChainMock.doFilter(any(), any()) }
        }

        should("refuse the token as invalid when the user cannot be identified by its claims") {
            // given
            SecurityContextHolder.getContext().authentication =
                JwtAuthenticationToken(JwtGenerators.generator.next(), emptyList())
            every { currentUserProviderMock.getCurrentUser() } throws
                MissingClaimException(JwtClaims.PREFERRED_USERNAME)

            // when & then
            shouldThrow<InvalidBearerTokenException> {
                cut.doFilter(MockHttpServletRequest(), MockHttpServletResponse(), filterChainMock)
            }
            verify(exactly = 0) { filterChainMock.doFilter(any(), any()) }
        }

        should("pass the request on without resolving when it carries no authentication") {
            // given
            SecurityContextHolder.clearContext()

            // when
            cut.doFilter(MockHttpServletRequest(), MockHttpServletResponse(), filterChainMock)

            // then
            verify(exactly = 0) { currentUserProviderMock.getCurrentUser() }
            verify(exactly = 1) { filterChainMock.doFilter(any(), any()) }
        }

        should("pass the request on without resolving when the authentication is not token based") {
            // given
            SecurityContextHolder.getContext().authentication =
                TestingAuthenticationToken("principal", "credentials")

            // when
            cut.doFilter(MockHttpServletRequest(), MockHttpServletResponse(), filterChainMock)

            // then
            verify(exactly = 0) { currentUserProviderMock.getCurrentUser() }
            verify(exactly = 1) { filterChainMock.doFilter(any(), any()) }
        }
    }
})
