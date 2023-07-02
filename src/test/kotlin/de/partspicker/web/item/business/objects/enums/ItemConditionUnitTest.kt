package de.partspicker.web.item.business.objects.enums

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ItemConditionUnitTest : ShouldSpec({
    context("isUsable") {
        data class IsUsable(
            val itemCondition: ItemCondition,
            val result: Boolean
        ) : WithDataTestName {
            override fun dataTestName() = "should return $result for condition $itemCondition"
        }

        withData(
            IsUsable(ItemCondition.WRAPPED, true),
            IsUsable(ItemCondition.NEW, true),
            IsUsable(ItemCondition.USED, true),
            IsUsable(ItemCondition.REPAIRABLE, false),
            IsUsable(ItemCondition.BROKEN, false),
            IsUsable(ItemCondition.UNKNOWN, false)
        ) { (condition, expectedResult) ->
            condition.isUsable() shouldBe expectedResult
        }
    }
})
