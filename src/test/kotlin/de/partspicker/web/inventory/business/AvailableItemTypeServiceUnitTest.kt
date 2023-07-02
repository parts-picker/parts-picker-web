package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.business.objects.AvailableItemType
import de.partspicker.web.inventory.persistence.AvailableItemTypeSearchRepository
import de.partspicker.web.inventory.persistence.results.AvailableItemTypeResult
import de.partspicker.web.workflow.business.WorkflowInteractionService
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainOnly
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AvailableItemTypeServiceUnitTest : ShouldSpec({
    val availableItemTypeSearchRepositoryMock = mockk<AvailableItemTypeSearchRepository>()
    val workflowInteractionServiceMock = mockk<WorkflowInteractionService>()
    val cut = AvailableItemTypeService(
        availableItemTypeSearchRepository = availableItemTypeSearchRepositoryMock,
        workflowInteractionService = workflowInteractionServiceMock
    )

    context("searchByName") {
        should("call the item type search repository & return the results") {
            // given
            val projectId = 1L
            val status = "status"
            val queryName = "some query"

            val itemToReturn = AvailableItemTypeResult(1, "item")

            every { workflowInteractionServiceMock.readProjectStatus(projectId) } returns status
            every {
                availableItemTypeSearchRepositoryMock.searchByNameFilterRequired(queryName, projectId)
            } returns listOf(itemToReturn)

            // when
            val returnedItems = cut.searchByName(queryName, projectId)

            // then
            verify(exactly = 1) {
                availableItemTypeSearchRepositoryMock.searchByNameFilterRequired(queryName, projectId)
            }

            returnedItems shouldContainOnly listOf(
                AvailableItemType(
                    id = itemToReturn.id,
                    name = itemToReturn.name,
                    projectId = projectId,
                    projectStatus = status
                )
            )
        }
    }
})
