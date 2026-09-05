package de.partspicker.web.common.security

import de.partspicker.web.common.security.exceptions.MissingClaimException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Provisions & updates the user of the given token if required.
 */
@Component
class UserProvisioningFilter(
    private val currentUserProvider: CurrentUserProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (SecurityContextHolder.getContext().authentication is JwtAuthenticationToken) {
            try {
                this.currentUserProvider.getCurrentUser()
            } catch (exception: MissingClaimException) {
                // refuse as invalid if any required claim cannot be used or is missing
                throw InvalidBearerTokenException(exception.message, exception)
            }
        }

        filterChain.doFilter(request, response)
    }
}
