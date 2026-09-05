package de.partspicker.web.test.generators

import de.partspicker.web.common.persistence.entities.CreationInfo
import de.partspicker.web.user.persistence.entities.UserEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.instant
import java.time.Instant

object CreationInfoGenerators {
    val generator: Arb<CreationInfo> = UserEntityGenerators.humanGenerator.flatMap { generatorFor(it) }

    /**
     * Returns an arbitrary generator that generates creation info naming the given user as its creator.
     */
    fun generatorFor(createdBy: UserEntity): Arb<CreationInfo> = arbitrary {
        CreationInfo(
            createdBy = createdBy,
            createdOn = Arb.instant(Instant.EPOCH, Instant.now()).bind()
        )
    }
}
