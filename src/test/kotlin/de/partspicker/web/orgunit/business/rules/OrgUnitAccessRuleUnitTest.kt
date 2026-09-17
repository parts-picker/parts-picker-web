package de.partspicker.web.orgunit.business.rules

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.orgunit.business.exceptions.OrgUnitAccessDeniedException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class OrgUnitAccessRuleUnitTest : ShouldSpec({

    val orgUnitId = 42L

    fun ruleFor(grantedLevel: AccessLevel, requiredLevel: AccessLevel) =
        OrgUnitAccessRule(grantedLevel, requiredLevel, orgUnitId)

    context("valid") {
        data class LevelData(
            val grantedLevel: AccessLevel,
            val requiredLevel: AccessLevel,
            val outcome: String
        ) : WithDataTestName {
            override fun dataTestName() =
                "should $outcome for ${this.grantedLevel} against required ${this.requiredLevel}"
        }

        withData(
            LevelData(AccessLevel.READ, AccessLevel.READ, "pass"),
            LevelData(AccessLevel.USE, AccessLevel.READ, "pass"),
            LevelData(AccessLevel.USE, AccessLevel.USE, "pass"),
            LevelData(AccessLevel.MAINTAIN, AccessLevel.READ, "pass"),
            LevelData(AccessLevel.CONFIGURE, AccessLevel.USE, "pass"),
            LevelData(AccessLevel.CONFIGURE, AccessLevel.CONFIGURE, "pass"),
            LevelData(AccessLevel.MAINTAIN, AccessLevel.USE, "pass"),
            LevelData(AccessLevel.MAINTAIN, AccessLevel.CONFIGURE, "pass"),
            LevelData(AccessLevel.MAINTAIN, AccessLevel.MAINTAIN, "pass")
        ) { data ->
            shouldNotThrowAny { ruleFor(data.grantedLevel, data.requiredLevel).valid() }
        }

        withData(
            LevelData(AccessLevel.NONE, AccessLevel.READ, "refuse"),
            LevelData(AccessLevel.NONE, AccessLevel.USE, "refuse"),
            LevelData(AccessLevel.NONE, AccessLevel.MAINTAIN, "refuse"),
            LevelData(AccessLevel.READ, AccessLevel.USE, "refuse"),
            LevelData(AccessLevel.READ, AccessLevel.MAINTAIN, "refuse"),
            LevelData(AccessLevel.USE, AccessLevel.CONFIGURE, "refuse"),
            LevelData(AccessLevel.USE, AccessLevel.MAINTAIN, "refuse"),
            LevelData(AccessLevel.CONFIGURE, AccessLevel.MAINTAIN, "refuse")
        ) { data ->
            shouldThrow<OrgUnitAccessDeniedException> { ruleFor(data.grantedLevel, data.requiredLevel).valid() }
        }

        should("name the required level & the org unit when refusing") {
            val exception = shouldThrow<OrgUnitAccessDeniedException> {
                ruleFor(AccessLevel.READ, AccessLevel.MAINTAIN).valid()
            }

            exception.message shouldBe "This action requires at least MAINTAIN in org unit with id $orgUnitId"
        }
    }
})
