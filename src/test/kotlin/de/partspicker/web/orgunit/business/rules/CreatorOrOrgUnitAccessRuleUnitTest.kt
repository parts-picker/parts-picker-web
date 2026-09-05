package de.partspicker.web.orgunit.business.rules

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.orgunit.business.exceptions.CreatorOrOrgUnitAccessDeniedException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class CreatorOrOrgUnitAccessRuleUnitTest : ShouldSpec({

    val orgUnitId = 42L
    val creatorId = 1L
    val otherUserId = 2L

    fun ruleFor(currentUserId: Long, grantedLevel: AccessLevel) = CreatorOrOrgUnitAccessRule(
        objectCreatedById = creatorId,
        currentUserId = currentUserId,
        grantedLevel = grantedLevel,
        requiredLevel = AccessLevel.MAINTAIN,
        orgUnitId = orgUnitId
    )

    context("valid") {
        data class AccessData(
            val currentUserId: Long,
            val grantedLevel: AccessLevel,
            val name: String
        ) : WithDataTestName {
            override fun dataTestName() = this.name
        }

        withData(
            AccessData(creatorId, AccessLevel.READ, "should pass for the creator holding READ"),
            AccessData(creatorId, AccessLevel.EDIT, "should pass for the creator holding EDIT"),
            AccessData(creatorId, AccessLevel.MAINTAIN, "should pass for the creator holding MAINTAIN"),
            AccessData(otherUserId, AccessLevel.MAINTAIN, "should pass for another member holding MAINTAIN")
        ) { data ->
            shouldNotThrowAny { ruleFor(data.currentUserId, data.grantedLevel).valid() }
        }

        withData(
            AccessData(otherUserId, AccessLevel.READ, "should refuse another member holding READ"),
            AccessData(otherUserId, AccessLevel.EDIT, "should refuse another member holding EDIT"),
            AccessData(creatorId, AccessLevel.NONE, "should refuse the creator once they left the org unit"),
            AccessData(otherUserId, AccessLevel.NONE, "should refuse anyone who is no member of the org unit")
        ) { data ->
            shouldThrow<CreatorOrOrgUnitAccessDeniedException> {
                ruleFor(data.currentUserId, data.grantedLevel).valid()
            }
        }

        should("name the required level & the creator exception when refusing") {
            val exception = shouldThrow<CreatorOrOrgUnitAccessDeniedException> {
                ruleFor(otherUserId, AccessLevel.READ).valid()
            }

            exception.message shouldBe "This action requires at least MAINTAIN in org unit with id $orgUnitId " +
                "or being the creator of the target of the action"
        }
    }
})
