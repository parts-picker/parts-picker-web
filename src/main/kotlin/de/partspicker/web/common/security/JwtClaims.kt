package de.partspicker.web.common.security

/**
 * Names of the token claims read by this application.
 */
object JwtClaims {
    const val ISSUER = "iss"
    const val SUBJECT = "sub"
    const val PREFERRED_USERNAME = "preferred_username"
    const val NAME = "name"
    const val REALM_ACCESS = "realm_access"
    const val ROLES = "roles"
}
