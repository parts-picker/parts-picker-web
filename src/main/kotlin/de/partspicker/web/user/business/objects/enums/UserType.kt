package de.partspicker.web.user.business.objects.enums

import de.partspicker.web.user.persistence.entities.enums.UserTypeEntity

enum class UserType {
    HUMAN,
    SYSTEM;

    companion object {
        fun from(userTypeEntity: UserTypeEntity) = when (userTypeEntity) {
            UserTypeEntity.HUMAN -> HUMAN
            UserTypeEntity.SYSTEM -> SYSTEM
        }
    }
}
