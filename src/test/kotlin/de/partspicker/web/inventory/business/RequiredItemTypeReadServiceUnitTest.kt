package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.persistence.RequiredItemTypeRepository
import de.partspicker.web.test.generators.id
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.property.Arb
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class RequiredItemTypeReadServiceUnitTest : ShouldSpec({
    val requiredItemTypeRepositoryMock = mockk<RequiredItemTypeRepository>()
    val inventoryItemReadServiceMock = mockk<InventoryItemReadService>()
    val cut = RequiredItemTypeReadService(
        requiredItemTypeRepository = requiredItemTypeRepositoryMock,
        inventoryItemReadService = inventoryItemReadServiceMock
    )

    context("readAllByProjectId") {
        should("call repository with default sorted pageable when given pageable is unsorted") {
            // given
            val projectId = Arb.id().single()
            val unsortedPageable = PageRequest.of(0, 10, Sort.unsorted())
            val expectedPageable = unsortedPageable.withSort(Sort.by(RequiredItemTypeReadService.DEFAULT_SORT))

            every {
                requiredItemTypeRepositoryMock.findAllByProjectId(projectId, expectedPageable)
            } returns Page.empty()

            // when
            val requiredItemTypes = cut.readAllByProjectId(projectId = projectId, pageable = unsortedPageable)

            // then
            requiredItemTypes shouldHaveSize 0
            verify { requiredItemTypeRepositoryMock.findAllByProjectId(projectId, expectedPageable) }
        }

        should("call repository with given pageable when given pageable is sorted") {
            // given
            val projectId = Arb.id().single()
            val pageable = PageRequest.of(0, 10, Sort.by("some.sort"))

            every {
                requiredItemTypeRepositoryMock.findAllByProjectId(projectId, pageable)
            } returns Page.empty()

            // when
            val requiredItemTypes = cut.readAllByProjectId(projectId = projectId, pageable = pageable)

            // then
            requiredItemTypes shouldHaveSize 0
            verify { requiredItemTypeRepositoryMock.findAllByProjectId(projectId, pageable) }
        }
    }
})
