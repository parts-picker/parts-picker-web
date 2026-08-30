package de.partspicker.web.user.business.objects

import de.partspicker.web.user.business.objects.enums.UserType
import de.partspicker.web.user.persistence.entities.UserEntity

data class User(
    val id: Long = 0,
    val issuer: String,
    val subject: String,
    val username: String,
    val displayName: String?,
    val type: UserType
) {
    companion object {
        fun from(userEntity: UserEntity) = User(
            id = userEntity.id,
            issuer = userEntity.issuer,
            subject = userEntity.subject,
            username = userEntity.username,
            displayName = userEntity.displayName,
            type = UserType.from(userEntity.type)
        )
    }
}
