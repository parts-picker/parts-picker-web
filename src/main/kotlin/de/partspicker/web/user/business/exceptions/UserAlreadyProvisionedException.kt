package de.partspicker.web.user.business.exceptions

import de.partspicker.web.user.business.objects.UserIdentity

class UserAlreadyProvisionedException(userIdentity: UserIdentity, cause: Throwable) : RuntimeException(
    "User with subject ${userIdentity.subject} of issuer ${userIdentity.issuer} was already created",
    cause
)
