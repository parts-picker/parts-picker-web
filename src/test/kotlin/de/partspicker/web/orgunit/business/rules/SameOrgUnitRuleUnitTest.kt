package de.partspicker.web.orgunit.business.rules

import de.partspicker.web.common.business.exceptions.CrossOrgUnitReferenceException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class SameOrgUnitRuleUnitTest : ShouldSpec({

    val orgUnitId = 42L
    val otherOrgUnitId = 43L

    context("valid") {
        should("pass for two objects of the same org unit") {
            shouldNotThrowAny { SameOrgUnitRule(orgUnitId, orgUnitId).valid() }
        }

        should("throw CrossOrgUnitReferenceException for objects of different org units") {
            shouldThrow<CrossOrgUnitReferenceException> { SameOrgUnitRule(orgUnitId, otherOrgUnitId).valid() }
        }

        should("name both org units when refusing") {
            val exception = shouldThrow<CrossOrgUnitReferenceException> {
                SameOrgUnitRule(orgUnitId, otherOrgUnitId).valid()
            }

            exception.message shouldBe
                "Objects of org unit $orgUnitId & org unit $otherOrgUnitId cannot be linked to each other"
        }
    }
})
