package de.partspicker.web.inventory.business

import de.partspicker.web.common.business.exceptions.WrongNodeNameRuleException
import de.partspicker.web.inventory.business.objects.AvailableItemType
import de.partspicker.web.inventory.persistence.AvailableItemTypeSearchRepository
import de.partspicker.web.inventory.persistence.results.AvailableItemTypeResult
import de.partspicker.web.project.business.ProjectService
import de.partspicker.web.test.generators.ProjectGenerators
import de.partspicker.web.workflow.business.exceptions.InstanceInactiveException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AvailableItemTypeServiceUnitTest : ShouldSpec({
    val availableItemTypeSearchRepositoryMock = mockk<AvailableItemTypeSearchRepository>()
    val projectServiceMock = mockk<ProjectService>()
    val cut = AvailableItemTypeService(
        availableItemTypeSearchRepository = availableItemTypeSearchRepositoryMock,
        projectService = projectServiceMock
    )

    context("searchByName") {
        should("call the item type search repository & return the results") {
            // given
            val projectId = 1L
            val queryName = "some query"
            val project = ProjectGenerators.generator.single().copy(status = "planning", active = true)

            val itemToReturn = AvailableItemTypeResult(1, "item")

            every { projectServiceMock.read(projectId) } returns project
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
                    projectStatus = project.status
                )
            )
        }

        should("throw WrongNodeNameRuleException when project status is not 'planning'") {
            // given
            val projectId = 1L
            val queryName = "some query"
            val project = ProjectGenerators.generator.single().copy(status = "not-planning", active = true)

            val itemToReturn = AvailableItemTypeResult(1, "item")

            every { projectServiceMock.read(projectId) } returns project
            every {
                availableItemTypeSearchRepositoryMock.searchByNameFilterRequired(queryName, projectId)
            } returns listOf(itemToReturn)

            // when
            val exception = shouldThrow<WrongNodeNameRuleException> { cut.searchByName(queryName, projectId) }

            // then
            exception.message shouldBe "Expected project status is planning, but actual status is ${project.status}"
        }

        should("throw InstanceInactiveException when project is not active") {
            // given
            val projectId = 1L
            val queryName = "some query"
            val project = ProjectGenerators.generator.single().copy(status = "planning", active = false)

            val itemToReturn = AvailableItemTypeResult(1, "item")

            every { projectServiceMock.read(projectId) } returns project
            every {
                availableItemTypeSearchRepositoryMock.searchByNameFilterRequired(queryName, projectId)
            } returns listOf(itemToReturn)

            // when
            val exception = shouldThrow<InstanceInactiveException> { cut.searchByName(queryName, projectId) }

            // then
            exception.message shouldBe "The instance with the given id ${project.workflowInstanceId} is inactive &" +
                " cannot be modified"
        }
    }
})
