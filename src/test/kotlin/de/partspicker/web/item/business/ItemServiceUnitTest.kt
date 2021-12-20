package de.partspicker.web.item.business

import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.objects.Item
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.test.generators.ItemEntityGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class ItemServiceUnitTest : ShouldSpec({

    val itemRepository = mockk<ItemRepository>()
    val cut = ItemService(
        itemRepository = itemRepository
    )

    context("getItems") {
        should("return all items") {
            // given
            val itemEntities = listOf(
                ItemEntityGenerators.generator.next(),
                ItemEntityGenerators.generator.next()
            )
            every { itemRepository.findAll() } returns itemEntities

            // when
            val returnedItems = cut.getItems()

            // then
            returnedItems shouldBe Item.AsList.from(itemEntities)
        }

        should("return empty list when no items available") {
            // given
            every { itemRepository.findAll() } returns emptyList()

            // when
            val returnedItems = cut.getItems()

            // then
            returnedItems shouldBe emptyList()
        }
    }

    context("getItem") {
        should("return correct item when given existent id") {
            // given
            val itemEntity = ItemEntityGenerators.generator.next()
            every { itemRepository.findById(itemEntity.id!!) } returns Optional.of(itemEntity)

            // when
            val returnedItem = cut.getItemById(itemEntity.id!!)

            // then
            returnedItem shouldBe Item.from(itemEntity)
        }

        should("throw ItemNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { itemRepository.findById(randomId) } returns Optional.empty()

            // when
            val exception = shouldThrow<ItemNotFoundException> {
                cut.getItemById(randomId)
            }

            // then
            exception.message shouldBe "Item with id $randomId could not be found"
        }
    }
})
