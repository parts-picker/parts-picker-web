package de.partspicker.web.item.business

import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.ItemType
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.test.generators.ItemTypeEntityGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class ItemTypeServiceUnitTest : ShouldSpec({

    val itemTypeRepository = mockk<ItemTypeRepository>()
    val itemService = mockk<ItemService>()
    val cut = ItemTypeService(
        itemTypeRepository = itemTypeRepository,
        itemService = itemService
    )

    context("getItemTypes") {
        should("return all itemTypes") {
            // given
            val itemTypeEntities = listOf(
                ItemTypeEntityGenerators.generator.next(),
                ItemTypeEntityGenerators.generator.next(),
                ItemTypeEntityGenerators.generator.next(),
            )
            every { itemTypeRepository.findAll() } returns itemTypeEntities

            // when
            val returnedItemTypes = cut.getItemTypes()

            // then
            returnedItemTypes shouldBe ItemType.AsList.from(itemTypeEntities)
        }

        should("return empty list when no itemTypes available") {
            // given
            every { itemTypeRepository.findAll() } returns emptyList()

            // when
            val returnedItemTypes = cut.getItemTypes()

            // then
            returnedItemTypes shouldBe emptyList()
        }
    }

    context("getItemTypeId") {
        should("return correct itemType when given existent id") {
            // given
            val itemTypeEntity = ItemTypeEntityGenerators.generator.next()
            every { itemTypeRepository.findById(itemTypeEntity.id!!) } returns Optional.of(itemTypeEntity)

            // when
            val returnedItemType = cut.getItemTypeById(itemTypeEntity.id!!)

            // then
            returnedItemType shouldBe ItemType.from(itemTypeEntity)
        }

        should("throw ItemTypeNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { itemTypeRepository.findById(randomId) } returns Optional.empty()

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.getItemTypeById(randomId)
            }

            // then
            exception.message shouldBe "ItemType with id $randomId could not be found"
        }
    }

    context("deleteItemTypeById") {

        should("delete the itemType with the given id & all items & return the amount of items deleted") {
            // given
            val id = Arb.long(min = 1).next()
            every { itemTypeRepository.existsById(id) } returns true

            val amountOfItemsDeleted = Arb.long(min = 1).next()
            every { itemService.deleteItemsForItemType(id) } returns amountOfItemsDeleted
            every { itemTypeRepository.deleteById(id) } returns Unit

            // when
            val returnedAmountOfDeletedItems = cut.deleteItemTypeById(id)

            // then
            verify(exactly = 1) {
                itemService.deleteItemsForItemType(id)
                itemTypeRepository.deleteById(id)
            }
            returnedAmountOfDeletedItems shouldBe amountOfItemsDeleted
        }

        should("throw ItemTypeNotFoundException when given non-existent id") {
            // given
            val nonExistingId = Arb.long(min = 1).next()
            every { itemTypeRepository.existsById(nonExistingId) } returns false

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.deleteItemTypeById(nonExistingId)
            }

            // then
            exception.message shouldBe "ItemType with id $nonExistingId could not be found"
        }
    }
})
