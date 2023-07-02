package de.partspicker.web.inventory.business.rules

import de.partspicker.web.inventory.business.exceptions.RequiredAmountEqualOrSmallerThanAssignedAmountRuleException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec

class RequiredGreaterAssignedAmountRuleUnitTest : ShouldSpec({
    should("be valid when requiredAmount is greater than assignedAmount") {
        RequiredGreaterAssignedAmountRule(5L, 4L).valid()
    }

    should("be invalid when requiredAmount equals assignedAmount") {
        shouldThrow<RequiredAmountEqualOrSmallerThanAssignedAmountRuleException> {
            RequiredGreaterAssignedAmountRule(4L, 4L).valid()
        }
    }

    should("be invalid when requiredAmount is smaller than assignedAmount") {
        shouldThrow<RequiredAmountEqualOrSmallerThanAssignedAmountRuleException> {
            RequiredGreaterAssignedAmountRule(3L, 4L).valid()
        }
    }
})
