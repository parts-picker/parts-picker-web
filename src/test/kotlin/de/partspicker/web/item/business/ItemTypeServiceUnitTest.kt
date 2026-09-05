package de.partspicker.web.item.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.business.objects.CreateItemType
import de.partspicker.web.item.business.objects.ItemType
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.orgunit.business.exceptions.CreatorOrOrgUnitAccessDeniedException
import de.partspicker.web.orgunit.business.exceptions.OrgUnitAccessDeniedException
import de.partspicker.web.orgunit.persistence.OrgUnitRepository
import de.partspicker.web.test.generators.ItemTypeEntityGenerators
import de.partspicker.web.test.generators.UserEntityGenerators
import de.partspicker.web.test.util.TestConstants.CRUD_REPOSITORY_EXTENSIONS
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull

class ItemTypeServiceUnitTest : ShouldSpec({

    val itemTypeRepositoryMock = mockk<ItemTypeRepository>()
    val itemRepositoryMock = mockk<ItemRepository>()
    val orgUnitRepositoryMock = mockk<OrgUnitRepository>()
    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = ItemTypeService(
        itemTypeRepository = itemTypeRepositoryMock,
        itemRepository = itemRepositoryMock,
        orgUnitRepository = orgUnitRepositoryMock,
        orgUnitAccessService = orgUnitAccessServiceMock
    )

    val currentUser = UserEntityGenerators.humanGenerator.next()

    beforeSpec {
        mockkStatic(CRUD_REPOSITORY_EXTENSIONS)
    }

    afterSpec {
        unmockkStatic(CRUD_REPOSITORY_EXTENSIONS)
    }

    beforeTest {
        every { orgUnitAccessServiceMock.requireAtLeast(any(), any()) } returns Unit
        every { orgUnitAccessServiceMock.requireMemberCreatorOrAtLeast(any(), any(), any()) } returns Unit
        every { orgUnitAccessServiceMock.currentUser() } returns currentUser
    }

    afterTest {
        clearMocks(itemTypeRepositoryMock, itemRepositoryMock, orgUnitAccessServiceMock)
    }

    context("create") {
        should("refuse & not store when the caller may not edit the given org unit") {
            // given
            every {
                orgUnitAccessServiceMock.requireAtLeast(1L, AccessLevel.EDIT)
            } throws OrgUnitAccessDeniedException(1L, AccessLevel.EDIT)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> {
                cut.create(1L, CreateItemType(name = "a name", description = null))
            }

            verify(exactly = 0) { itemTypeRepositoryMock.save(any()) }
        }

        should("create new itemType & return it") {
            // given
            val entity = ItemTypeEntityGenerators.generator.next()
            every { orgUnitRepositoryMock.getReferenceById(entity.orgUnit.id) } returns entity.orgUnit
            every { itemTypeRepositoryMock.save(any()) } returns entity

            // when
            val returnedItemType = cut.create(
                entity.orgUnit.id,
                CreateItemType(name = entity.name!!, description = entity.description)
            )

            // then
            verify(exactly = 1) {
                itemTypeRepositoryMock.save(any())
            }
            returnedItemType shouldBe ItemType.from(entity)
        }
    }

    context("findAllForOrgUnit") {
        should("return all itemTypes of the given org unit") {
            // given
            val itemTypePage: Page<ItemTypeEntity> = PageImpl(
                listOf(
                    ItemTypeEntityGenerators.generator.next(),
                    ItemTypeEntityGenerators.generator.next(),
                    ItemTypeEntityGenerators.generator.next(),
                )
            )
            every { itemTypeRepositoryMock.findAllByOrgUnitId(1L, Pageable.unpaged()) } returns itemTypePage

            // when
            val returnedItemTypes = cut.findAllForOrgUnit(1L, Pageable.unpaged())

            // then
            returnedItemTypes.content shouldBe ItemType.AsList.from(itemTypePage.content)
        }

        should("return empty list when no itemTypes available") {
            // given
            every { itemTypeRepositoryMock.findAllByOrgUnitId(1L, Pageable.unpaged()) } returns Page.empty()

            // when
            val returnedItemTypes = cut.findAllForOrgUnit(1L, Pageable.unpaged())

            // then
            returnedItemTypes.content shouldBe emptyList()
        }
    }

    context("getById") {
        should("return correct itemType when given existent id") {
            // given
            val itemTypeEntity = ItemTypeEntityGenerators.generator.next()
            every { itemTypeRepositoryMock.findByIdOrNull(itemTypeEntity.id) } returns itemTypeEntity

            // when
            val returnedItemType = cut.getById(itemTypeEntity.id)

            // then
            returnedItemType shouldBe ItemType.from(itemTypeEntity)
        }

        should("throw ItemTypeNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { itemTypeRepositoryMock.findByIdOrNull(randomId) } returns null

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.getById(randomId)
            }

            // then
            exception.message shouldBe "ItemType with id $randomId could not be found"
        }
    }

    context("update") {

        should("update the itemType with the given id & return it") {
            // given
            val id = 12L
            val entity = ItemTypeEntityGenerators.generator.next().copy(id = id)
            every { itemTypeRepositoryMock.findByIdOrNull(id) } returns entity
            every { itemTypeRepositoryMock.save(entity) } returns entity

            // when
            val updatedType = cut.update(id, name = "new name", description = "new description")

            // then
            updatedType.name shouldBe "new name"
            updatedType.description shouldBe "new description"

            verify(exactly = 1) {
                itemTypeRepositoryMock.save(entity)
            }
        }

        should("keep the org unit & the creation info of the updated itemType") {
            // given
            val id = 12L
            val entity = ItemTypeEntityGenerators.generator.next().copy(id = id)
            val originalOrgUnit = entity.orgUnit
            val originalCreation = entity.creation
            every { itemTypeRepositoryMock.findByIdOrNull(id) } returns entity
            every { itemTypeRepositoryMock.save(entity) } returns entity

            // when
            cut.update(id, name = "new name", description = null)

            // then
            entity.orgUnit shouldBe originalOrgUnit
            entity.creation shouldBe originalCreation
        }

        should("throw ItemTypeNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { itemTypeRepositoryMock.findByIdOrNull(randomId) } returns null

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.update(randomId, name = "new name", description = null)
            }

            // then
            exception.message shouldBe "ItemType with id $randomId could not be found"
        }
    }

    context("delete") {

        should("refuse & delete nothing when the caller neither created the itemType nor maintains its org unit") {
            // given
            val itemTypeEntity = ItemTypeEntityGenerators.generator.next()
            every { itemTypeRepositoryMock.findByIdOrNull(itemTypeEntity.id) } returns itemTypeEntity
            every {
                orgUnitAccessServiceMock.requireMemberCreatorOrAtLeast(any(), any(), AccessLevel.MAINTAIN)
            } throws CreatorOrOrgUnitAccessDeniedException(itemTypeEntity.orgUnit.id, AccessLevel.MAINTAIN)

            // when & then
            shouldThrow<CreatorOrOrgUnitAccessDeniedException> { cut.delete(itemTypeEntity.id) }

            verify(exactly = 0) {
                itemRepositoryMock.deleteAllByTypeId(any())
                itemTypeRepositoryMock.delete(any())
            }
        }

        should("delete the itemType with the given id & all items & return the amount of items deleted") {
            // given
            val id = Arb.long(min = 1).next()
            val itemTypeEntity = ItemTypeEntityGenerators.generator.next().copy(id = id)
            every { itemTypeRepositoryMock.findByIdOrNull(id) } returns itemTypeEntity

            val amountOfItemsDeleted = Arb.long(min = 1).next()
            every { itemRepositoryMock.deleteAllByTypeId(id) } returns amountOfItemsDeleted
            every { itemTypeRepositoryMock.delete(itemTypeEntity) } returns Unit

            // when
            val returnedAmountOfDeletedItems = cut.delete(id)

            // then
            verify(exactly = 1) {
                itemRepositoryMock.deleteAllByTypeId(id)
                itemTypeRepositoryMock.delete(itemTypeEntity)
            }
            returnedAmountOfDeletedItems shouldBe amountOfItemsDeleted
        }

        should("throw ItemTypeNotFoundException when given non-existent id") {
            // given
            val nonExistingId = Arb.long(min = 1).next()
            every { itemTypeRepositoryMock.findByIdOrNull(nonExistingId) } returns null

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.delete(nonExistingId)
            }

            // then
            exception.message shouldBe "ItemType with id $nonExistingId could not be found"
        }
    }
})
