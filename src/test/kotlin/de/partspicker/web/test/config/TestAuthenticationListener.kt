package de.partspicker.web.test.config

import de.partspicker.web.test.util.TestUsers
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * Puts a request & an authenticated user on the thread of every test, which the request scoped
 * CurrentUserProvider requires.
 */
object TestAuthenticationListener : BeforeTestListener, AfterTestListener {

    override suspend fun beforeAny(testCase: TestCase) {
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(MockHttpServletRequest()))
        SecurityContextHolder.getContext().authentication = JwtAuthenticationToken(TestUsers.jwt())
    }

    override suspend fun afterAny(testCase: TestCase, result: TestResult) {
        SecurityContextHolder.clearContext()
        RequestContextHolder.resetRequestAttributes()
    }
}
