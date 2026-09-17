package de.partspicker.web.orgunit.api.responses

import de.partspicker.web.common.business.objects.enums.AccessLevel

enum class AccessLevelResponse {
    NONE,
    READ,
    USE,
    CONFIGURE,
    MAINTAIN;

    companion object {
        fun from(accessLevel: AccessLevel) = when (accessLevel) {
            AccessLevel.NONE -> NONE
            AccessLevel.READ -> READ
            AccessLevel.USE -> USE
            AccessLevel.CONFIGURE -> CONFIGURE
            AccessLevel.MAINTAIN -> MAINTAIN
        }
    }
}
