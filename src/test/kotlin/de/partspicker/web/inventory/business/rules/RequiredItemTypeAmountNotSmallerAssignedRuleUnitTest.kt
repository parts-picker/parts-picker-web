package de.partspicker.web.inventory.business.rules

import de.partspicker.web.inventory.business.exceptions.RequiredItemTypeAmountSmallerThanAssignedException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec

class RequiredItemTypeAmountNotSmallerAssignedRuleUnitTest : ShouldSpec({

    should("be valid when requiredAmount equals assignedAmount") {
        RequiredItemTypeAmountNotSmallerAssignedRule(4L, 4L).valid()
    }

    should("be valid when requiredAmount is greater than assignedAmount") {
        RequiredItemTypeAmountNotSmallerAssignedRule(5L, 4L).valid()
    }

    should("be invalid when requiredAmount is smaller than assignedAmount") {
        shouldThrow<RequiredItemTypeAmountSmallerThanAssignedException> {
            RequiredItemTypeAmountNotSmallerAssignedRule(3L, 4L).valid()
        }
    }
})
