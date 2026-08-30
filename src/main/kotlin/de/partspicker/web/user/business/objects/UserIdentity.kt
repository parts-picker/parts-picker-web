package de.partspicker.web.user.business.objects

data class UserIdentity(
    val issuer: String,
    val subject: String,
    val username: String,
    val displayName: String?
)
