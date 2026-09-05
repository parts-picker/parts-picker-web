package de.partspicker.web.inventory.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.inventory.persistence.RequiredItemTypeRepository
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.orgunit.business.exceptions.OrgUnitAccessDeniedException
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.ProjectEntityGenerators
import de.partspicker.web.test.generators.id
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.property.Arb
import io.kotest.property.arbitrary.single
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class RequiredItemTypeReadServiceUnitTest : ShouldSpec({
    val requiredItemTypeRepositoryMock = mockk<RequiredItemTypeRepository>()
    val inventoryItemReadServiceMock = mockk<InventoryItemReadService>()
    val projectRepositoryMock = mockk<ProjectRepository>()
    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = RequiredItemTypeReadService(
        requiredItemTypeRepository = requiredItemTypeRepositoryMock,
        inventoryItemReadService = inventoryItemReadServiceMock,
        projectRepository = projectRepositoryMock,
        orgUnitAccessService = orgUnitAccessServiceMock
    )

    beforeTest {
        every { projectRepositoryMock.getNullableReferenceById(any()) } returns
            ProjectEntityGenerators.generator.single()
        every { orgUnitAccessServiceMock.requireAtLeast(any(), any()) } returns Unit
    }

    afterTest {
        clearMocks(requiredItemTypeRepositoryMock, projectRepositoryMock, orgUnitAccessServiceMock)
    }

    context("readAllByProjectId") {
        should("refuse & not read when the caller holds nothing in the org unit of the project") {
            // given
            val projectId = Arb.id().single()
            val projectEntity = ProjectEntityGenerators.generator.single()
            every { projectRepositoryMock.getNullableReferenceById(projectId) } returns projectEntity
            every {
                orgUnitAccessServiceMock.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.READ)
            } throws OrgUnitAccessDeniedException(projectEntity.orgUnit.id, AccessLevel.READ)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> {
                cut.readAllByProjectId(projectId = projectId, pageable = Pageable.unpaged())
            }

            verify(exactly = 0) { requiredItemTypeRepositoryMock.findAllByProjectId(any(), any()) }
        }

        should("throw ProjectNotFoundException when no project with the given id exists") {
            // given
            val projectId = Arb.id().single()
            every { projectRepositoryMock.getNullableReferenceById(projectId) } returns null

            // when & then
            shouldThrow<ProjectNotFoundException> {
                cut.readAllByProjectId(projectId = projectId, pageable = Pageable.unpaged())
            }
        }

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
