package de.partspicker.web.common.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

/**
 * Turns the keycloak realm roles present in `realm_access.roles` into spring security authorities.
 *
 * The default [org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter]
 * only reads top level claims, while keycloak claims are nested.
 */
@Component
class RealmRoleAuthoritiesConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    override fun convert(source: Jwt): Collection<GrantedAuthority> {
        val roles = source.getClaimAsMap(JwtClaims.REALM_ACCESS)
            ?.get(JwtClaims.ROLES) as? Collection<*> ?: emptyList<Any?>()

        return roles.filterIsInstance<String>()
            .filter { it.isNotBlank() }
            .map { SimpleGrantedAuthority("$ROLE_PREFIX$it") }
    }

    companion object {
        const val ROLE_PREFIX = "ROLE_"
    }
}
