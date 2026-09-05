package de.partspicker.web.common.persistence.entities

import de.partspicker.web.common.security.CurrentUserProvider
import de.partspicker.web.user.business.UserService
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant

/**
 * Builds the [CreationInfo] of an entity created by the user of the current request.
 */
@Component
class CreationInfoFactory(
    private val currentUserProvider: CurrentUserProvider,
    private val userService: UserService,
    private val clock: Clock
) {
    fun forCurrentUser() = CreationInfo(
        createdBy = this.userService.getReference(this.currentUserProvider.getCurrentUser().id),
        createdOn = Instant.now(this.clock)
    )
}
