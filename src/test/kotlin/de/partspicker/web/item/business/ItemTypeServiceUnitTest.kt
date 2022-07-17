package de.partspicker.web.item.business

import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.ItemType
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import de.partspicker.web.test.generators.ItemTypeEntityGenerators
import de.partspicker.web.test.generators.ItemTypeGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.Optional

class ItemTypeServiceUnitTest : ShouldSpec({

    val itemTypeRepositoryMock = mockk<ItemTypeRepository>()
    val itemServiceMock = mockk<ItemService>()
    val cut = ItemTypeService(
        itemTypeRepository = itemTypeRepositoryMock,
        itemService = itemServiceMock
    )

    context("create") {
        should("create new itemType & return it") {
            // given
            val entity = ItemTypeEntityGenerators.generator.next()
            every { itemTypeRepositoryMock.save(entity) } returns entity

            // when
            val returnedItemType = cut.create(ItemType.from(entity))

            // then
            verify(exactly = 1) {
                itemTypeRepositoryMock.save(any())
            }
            returnedItemType shouldBe ItemType.from(entity)
        }
    }

    context("getItemTypes") {
        should("return all itemTypes") {
            // given
            val itemTypePage: Page<ItemTypeEntity> = PageImpl(
                listOf(
                    ItemTypeEntityGenerators.generator.next(),
                    ItemTypeEntityGenerators.generator.next(),
                    ItemTypeEntityGenerators.generator.next(),
                )
            )
            every { itemTypeRepositoryMock.findAll(Pageable.unpaged()) } returns itemTypePage

            // when
            val returnedItemTypes = cut.getItemTypes(Pageable.unpaged())

            // then
            returnedItemTypes.content shouldBe ItemType.AsList.from(itemTypePage.content)
        }

        should("return empty list when no itemTypes available") {
            // given
            every { itemTypeRepositoryMock.findAll(Pageable.unpaged()) } returns Page.empty()

            // when
            val returnedItemTypes = cut.getItemTypes(Pageable.unpaged())

            // then
            returnedItemTypes.content shouldBe emptyList()
        }
    }

    context("getItemTypeId") {
        should("return correct itemType when given existent id") {
            // given
            val itemTypeEntity = ItemTypeEntityGenerators.generator.next()
            every { itemTypeRepositoryMock.findById(itemTypeEntity.id) } returns Optional.of(itemTypeEntity)

            // when
            val returnedItemType = cut.getItemTypeById(itemTypeEntity.id)

            // then
            returnedItemType shouldBe ItemType.from(itemTypeEntity)
        }

        should("throw ItemTypeNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { itemTypeRepositoryMock.findById(randomId) } returns Optional.empty()

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.getItemTypeById(randomId)
            }

            // then
            exception.message shouldBe "ItemType with id $randomId could not be found"
        }
    }

    context("update") {

        should("update the itemType with the given id & return it") {
            // given
            val id = 12L
            every { itemTypeRepositoryMock.existsById(id) } returns true

            val entity = ItemTypeEntityGenerators.generator.next().copy(id = id)
            val itemType = ItemType.from(entity)
            every { itemTypeRepositoryMock.save(entity) } returns entity

            // when
            val updatedType = cut.update(itemType)

            // then
            updatedType shouldBe itemType

            verify(exactly = 1) {
                itemTypeRepositoryMock.save(entity)
            }
        }

        should("throw ItemTypeNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { itemTypeRepositoryMock.existsById(randomId) } returns false

            val itemType = ItemTypeGenerators.generator.next().copy(id = randomId)

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.update(itemType)
            }

            // then
            exception.message shouldBe "ItemType with id $randomId could not be found"
        }
    }

    context("deleteItemTypeById") {

        should("delete the itemType with the given id & all items & return the amount of items deleted") {
            // given
            val id = Arb.long(min = 1).next()
            every { itemTypeRepositoryMock.existsById(id) } returns true

            val amountOfItemsDeleted = Arb.long(min = 1).next()
            every { itemServiceMock.deleteItemsForItemType(id) } returns amountOfItemsDeleted
            every { itemTypeRepositoryMock.deleteById(id) } returns Unit

            // when
            val returnedAmountOfDeletedItems = cut.deleteItemTypeById(id)

            // then
            verify(exactly = 1) {
                itemServiceMock.deleteItemsForItemType(id)
                itemTypeRepositoryMock.deleteById(id)
            }
            returnedAmountOfDeletedItems shouldBe amountOfItemsDeleted
        }

        should("throw ItemTypeNotFoundException when given non-existent id") {
            // given
            val nonExistingId = Arb.long(min = 1).next()
            every { itemTypeRepositoryMock.existsById(nonExistingId) } returns false

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.deleteItemTypeById(nonExistingId)
            }

            // then
            exception.message shouldBe "ItemType with id $nonExistingId could not be found"
        }
    }
})
