package de.partspicker.web.common.business.objects.enums

import de.partspicker.web.common.persistence.entities.enums.AccessLevelEntity

/**
 * Describes the level of access within a scope.
 *
 * Ordered from least to most access: comparisons & [isAtLeast] rely on the declaration order, so an additional level
 * must be inserted in the correct place.
 */
enum class AccessLevel {
    NONE,
    READ,
    EDIT,
    MAINTAIN;

    infix fun isAtLeast(other: AccessLevel) = this >= other

    companion object {
        fun from(accessLevelEntity: AccessLevelEntity?) = when (accessLevelEntity) {
            AccessLevelEntity.READ -> READ
            AccessLevelEntity.EDIT -> EDIT
            AccessLevelEntity.MAINTAIN -> MAINTAIN
            null -> NONE
        }
    }
}
