package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.inventory.persitance.RequiredItemTypeRepository
import de.partspicker.web.inventory.persitance.entities.RequiredItemTypeEntity
import de.partspicker.web.test.generators.RequiredItemTypeEntityGenerators
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class RequiredItemTypeServiceUnitTest : ShouldSpec({

    val requiredItemTypesRepositoryMock = mockk<RequiredItemTypeRepository>()
    val cut = RequiredItemTypeService(
        requiredItemTypeRepository = requiredItemTypesRepositoryMock
    )

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
                requiredItemTypesRepositoryMock
                    .findAllByProjectId(projectId, Pageable.unpaged())
            } returns requiredItemTypesPage

            // when
            val returnedRequiredItemTypes = cut.readAllByProjectId(projectId, Pageable.unpaged())

            // then
            returnedRequiredItemTypes shouldBe RequiredItemType.AsPage.from(requiredItemTypesPage)
        }
    }
})
