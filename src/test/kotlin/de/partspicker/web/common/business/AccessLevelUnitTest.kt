package de.partspicker.web.common.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.persistence.entities.enums.AccessLevelEntity
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class AccessLevelUnitTest : ShouldSpec({

    context("isAtLeast") {
        data class IsAtLeastData(
            val accessLevel: AccessLevel,
            val demandedAccessLevel: AccessLevel,
            val result: Boolean
        ) : WithDataTestName {
            override fun dataTestName() = "should return $result for $accessLevel against $demandedAccessLevel"
        }

        withData(
            IsAtLeastData(AccessLevel.NONE, AccessLevel.NONE, true),
            IsAtLeastData(AccessLevel.NONE, AccessLevel.READ, false),
            IsAtLeastData(AccessLevel.NONE, AccessLevel.EDIT, false),
            IsAtLeastData(AccessLevel.NONE, AccessLevel.MAINTAIN, false),
            IsAtLeastData(AccessLevel.READ, AccessLevel.NONE, true),
            IsAtLeastData(AccessLevel.READ, AccessLevel.READ, true),
            IsAtLeastData(AccessLevel.READ, AccessLevel.EDIT, false),
            IsAtLeastData(AccessLevel.READ, AccessLevel.MAINTAIN, false),
            IsAtLeastData(AccessLevel.EDIT, AccessLevel.NONE, true),
            IsAtLeastData(AccessLevel.EDIT, AccessLevel.READ, true),
            IsAtLeastData(AccessLevel.EDIT, AccessLevel.EDIT, true),
            IsAtLeastData(AccessLevel.EDIT, AccessLevel.MAINTAIN, false),
            IsAtLeastData(AccessLevel.MAINTAIN, AccessLevel.NONE, true),
            IsAtLeastData(AccessLevel.MAINTAIN, AccessLevel.READ, true),
            IsAtLeastData(AccessLevel.MAINTAIN, AccessLevel.EDIT, true),
            IsAtLeastData(AccessLevel.MAINTAIN, AccessLevel.MAINTAIN, true)
        ) { (accessLevel, demandedAccessLevel, expectedResult) ->
            accessLevel isAtLeast demandedAccessLevel shouldBe expectedResult
        }
    }

    context("companion.from") {
        data class FromData(
            val accessLevelEntity: AccessLevelEntity?,
            val result: AccessLevel
        ) : WithDataTestName {
            override fun dataTestName() = "should return $result for entity $accessLevelEntity"
        }

        withData(
            FromData(AccessLevelEntity.READ, AccessLevel.READ),
            FromData(AccessLevelEntity.EDIT, AccessLevel.EDIT),
            FromData(AccessLevelEntity.MAINTAIN, AccessLevel.MAINTAIN),
            FromData(null, AccessLevel.NONE)
        ) { (accessLevelEntity, expectedAccessLevel) ->
            AccessLevel.from(accessLevelEntity) shouldBe expectedAccessLevel
        }
    }

    context("ordering") {
        should("increase from none to maintain") {
            AccessLevel.entries shouldBe listOf(
                AccessLevel.NONE,
                AccessLevel.READ,
                AccessLevel.EDIT,
                AccessLevel.MAINTAIN
            )
        }
    }
})
