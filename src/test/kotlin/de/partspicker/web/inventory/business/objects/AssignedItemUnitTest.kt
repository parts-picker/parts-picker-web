package de.partspicker.web.inventory.business.objects

import de.partspicker.web.inventory.business.exceptions.AssignedItemMissingProjectRuleException
import de.partspicker.web.inventory.business.exceptions.InventoryItemMustBeUsableRuleException
import de.partspicker.web.inventory.business.objects.enums.InventoryItemCondition
import de.partspicker.web.item.persistance.entities.ItemEntity
import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import de.partspicker.web.item.persistance.entities.enums.ItemConditionEntity
import de.partspicker.web.item.persistance.entities.enums.ItemStatusEntity
import de.partspicker.web.test.generators.ProjectEntityGenerators
import de.partspicker.web.test.generators.inventory.AssignedItemGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import io.kotest.property.checkAll

class AssignedItemUnitTest : ShouldSpec({
    context("validation") {
        should("run when everything valid") {
            AssignedItem(
                itemId = 1L,
                itemTypeId = 1L,
                projectId = 1L,
                projectStatus = "planning",
                condition = InventoryItemCondition.NEW,
            )
        }

        should("throw IllegalArgumentException when given empty project status") {
            shouldThrow<IllegalArgumentException> {
                AssignedItem(
                    itemId = 1L,
                    itemTypeId = 1L,
                    projectId = 1L,
                    projectStatus = "",
                    condition = InventoryItemCondition.NEW,
                )
            }
        }
    }

    context("from") {
        should("run when everything valid") {
            checkAll(AssignedItemGenerators.generator) {
                // just run
            }
        }

        should("throw AssignedItemMissingProjectRuleException when given itemEntity with no project") {
            val itemId = 1L
            val exception = shouldThrow<AssignedItemMissingProjectRuleException> {
                AssignedItem.from(
                    ItemEntity(
                        id = itemId,
                        type = ItemTypeEntity(id = 1L),
                        assignedProject = null,
                        status = ItemStatusEntity.IN_STOCK,
                        condition = ItemConditionEntity.WRAPPED,
                        note = null
                    )
                )
            }

            exception.message shouldBe "Item with id '$itemId' has no project assigned"
        }

        should("throw InventoryItemMustBeUsableRuleException when given itemEntity with unusable condition") {
            val itemId = 1L
            val condition = ItemConditionEntity.BROKEN
            val expectedMessage =
                "Item with id $itemId must be usable to be assignable/assigned, but its condition is $condition"

            val exception = shouldThrow<InventoryItemMustBeUsableRuleException> {
                AssignedItem.from(
                    ItemEntity(
                        id = itemId,
                        type = ItemTypeEntity(id = 1),
                        assignedProject = ProjectEntityGenerators.generator.single(),
                        status = ItemStatusEntity.RESERVED,
                        condition = condition,
                        note = null
                    )
                )
            }

            exception.message shouldBe expectedMessage
        }
    }
})
