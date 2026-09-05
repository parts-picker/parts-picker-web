package de.partspicker.web.common.security

import de.partspicker.web.common.security.exceptions.MissingClaimException
import de.partspicker.web.common.security.exceptions.NoAuthenticatedUserException
import de.partspicker.web.user.business.UserService
import de.partspicker.web.user.business.exceptions.UserAlreadyProvisionedException
import de.partspicker.web.user.business.objects.User
import de.partspicker.web.user.business.objects.UserIdentity
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.context.WebApplicationContext

/**
 * Offers lazy and cached access to the current user.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
class CurrentUserProvider(
    private val userService: UserService
) {
    private val resolvedUser: User by lazy { resolveWithRetry(readIdentity()) }

    fun getCurrentUser() = this.resolvedUser

    /**
     * The current user as a reference for writing foreign keys, without loading the row again.
     */
    fun getCurrentUserEntity() = this.userService.getReference(this.resolvedUser.id)

    // helpers
    private fun resolveWithRetry(userIdentity: UserIdentity) = try {
        this.userService.resolve(userIdentity)
    } catch (ignored: UserAlreadyProvisionedException) {
        // another request created this user first, so retry resolving
        this.userService.resolve(userIdentity)
    }

    private fun readIdentity(): UserIdentity {
        val authentication = SecurityContextHolder.getContext().authentication as? JwtAuthenticationToken
            ?: throw NoAuthenticatedUserException()

        val jwt = authentication.token

        return UserIdentity(
            issuer = requiredClaim(jwt, JwtClaims.ISSUER),
            subject = requiredClaim(jwt, JwtClaims.SUBJECT),
            username = requiredClaim(jwt, JwtClaims.PREFERRED_USERNAME),
            displayName = jwt.getClaimAsString(JwtClaims.NAME)
        )
    }

    private fun requiredClaim(jwt: Jwt, claimName: String) =
        jwt.getClaimAsString(claimName) ?: throw MissingClaimException(claimName)
}
