package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.inventory.persitance.RequiredItemTypeRepository
import de.partspicker.web.inventory.persitance.entities.RequiredItemTypeEntity
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.item.persistance.ItemTypeRepository
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.RequiredItemTypeEntityGenerators
import de.partspicker.web.test.generators.RequiredItemTypeGenerators
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

class RequiredItemTypeServiceUnitTest : ShouldSpec({

    val requiredItemTypeRepositoryMock = mockk<RequiredItemTypeRepository>()
    val projectRepositoryMock = mockk<ProjectRepository>()
    val itemTypeRepositoryMock = mockk<ItemTypeRepository>()
    val cut = RequiredItemTypeService(
        requiredItemTypeRepository = requiredItemTypeRepositoryMock,
        projectRepository = projectRepositoryMock,
        itemTypeRepository = itemTypeRepositoryMock
    )

    context("create") {
        should("create a new required item type & return it") {
            // given
            val requiredItemType = RequiredItemTypeGenerators.generator.next()

            every { projectRepositoryMock.existsById(requiredItemType.projectId) } returns true
            every { itemTypeRepositoryMock.existsById(requiredItemType.itemType.id) } returns true
            every { requiredItemTypeRepositoryMock.save(any()) } returnsArgument 0

            // when
            val returnedRequiredItemType = cut.create(requiredItemType)

            verify(exactly = 1) {
                requiredItemTypeRepositoryMock.save(any())
            }

            returnedRequiredItemType.projectId shouldBe requiredItemType.projectId
            returnedRequiredItemType.itemType.id shouldBe requiredItemType.itemType.id
            returnedRequiredItemType.requiredAmount shouldBe requiredItemType.requiredAmount
        }

        should("throw ProjectNotFoundException when given non-existent project") {
            // given
            every { projectRepositoryMock.existsById(any()) } returns false

            val requiredItemType = RequiredItemTypeGenerators.generator.next()

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.create(requiredItemType)
            }

            // then
            exception.message shouldBe "Project with id ${requiredItemType.projectId} could not be found"
        }

        should("throw ItemTypeNotFoundException when given non-existent itemType") {
            // given
            every { projectRepositoryMock.existsById(any()) } returns true
            every { itemTypeRepositoryMock.existsById(any()) } returns false

            val requiredItemType = RequiredItemTypeGenerators.generator.next()

            // when
            val exception = shouldThrow<ItemTypeNotFoundException> {
                cut.create(requiredItemType)
            }

            // then
            exception.message shouldBe "ItemType with id ${requiredItemType.itemType.id} could not be found"
        }
    }

    context("readAllByProjectId") {
        should("return all required item types") {
            // given
            val projectId = Arb.long(min = 1).next()

            val requiredItemTypesPage: Page<RequiredItemTypeEntity> = PageImpl(
                listOf(
                    RequiredItemTypeEntityGenerators.generator.next(),
                    RequiredItemTypeEntityGenerators.generator.next()
                )
            )

            every {
                requiredItemTypeRepositoryMock
                    .findAllByProjectId(projectId, Pageable.unpaged())
            } returns requiredItemTypesPage

            // when
            val returnedRequiredItemTypes = cut.readAllByProjectId(projectId, Pageable.unpaged())

            // then
            returnedRequiredItemTypes shouldBe RequiredItemType.AsPage.from(requiredItemTypesPage)
        }
    }
})
