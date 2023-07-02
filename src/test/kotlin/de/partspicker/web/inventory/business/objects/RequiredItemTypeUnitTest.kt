package de.partspicker.web.inventory.business.objects

import de.partspicker.web.item.business.objects.ItemType
import de.partspicker.web.test.generators.inventory.RequiredItemTypeGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
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
                    itemType = ItemType(id = 1L),
                    assignedAmount = -1,
                    requiredAmount = 1
                )
            }
        }
    }
})
