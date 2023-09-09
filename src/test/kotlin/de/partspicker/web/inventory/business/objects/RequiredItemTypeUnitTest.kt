package de.partspicker.web.inventory.business.objects

import de.partspicker.web.item.business.objects.ItemType
import de.partspicker.web.test.generators.ItemTypeGenerators
import de.partspicker.web.test.generators.inventory.RequiredItemTypeGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import io.kotest.property.checkAll

class RequiredItemTypeUnitTest : ShouldSpec({
    context("validation") {
        should("run when everything valid") {
            checkAll(RequiredItemTypeGenerators.generator) {
                // just run
            }
        }

        should("throw IllegalArgumentException when given required amount smaller than one") {
            shouldThrow<IllegalArgumentException> {
                RequiredItemType(
                    projectId = 1L,
                    projectStatus = "planning",
                    itemType = ItemType(id = 1L),
                    assignedAmount = 0,
                    requiredAmount = 0
                )
            }
        }

        should("throw IllegalArgumentException when given assigned amount smaller than zero") {
            shouldThrow<IllegalArgumentException> {
                RequiredItemType(
                    projectId = 1L,
                    projectStatus = "planning",
                    itemType = ItemType(id = 1L),
                    assignedAmount = -1,
                    requiredAmount = 1
                )
            }
        }
    }

    context("isRequiredAmountAssigned") {
        should("return true when assignedAmount equals requiredAmount") {
            // given
            val cut = RequiredItemType(
                projectId = 1L,
                projectStatus = "planning",
                itemType = ItemTypeGenerators.generator.single(),
                assignedAmount = 2L,
                requiredAmount = 2L
            )

            // when
            val result = cut.isRequiredAmountAssigned()

            // then
            result shouldBe true
        }

        should("return false when assignedAmount not equal to requiredAmount") {
            // given
            val cut = RequiredItemType(
                projectId = 1L,
                projectStatus = "planning",
                itemType = ItemTypeGenerators.generator.single(),
                assignedAmount = 1L,
                requiredAmount = 2L
            )

            // when
            val result = cut.isRequiredAmountAssigned()

            // then
            result shouldBe false
        }
    }
})
