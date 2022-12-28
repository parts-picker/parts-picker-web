package de.partspicker.web.workflow.business

import de.partspicker.web.test.generators.workflow.WorkflowCreateGenerators
import de.partspicker.web.workflow.business.exceptions.WorkflowAlreadyExistsException
import de.partspicker.web.workflow.business.exceptions.WorkflowLatestVersionIsGreaterException
import de.partspicker.web.workflow.persistance.EdgeRepository
import de.partspicker.web.workflow.persistance.NodeRepository
import de.partspicker.web.workflow.persistance.WorkflowRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk

class WorkflowServiceUnitTest : ShouldSpec({

    val workflowRepositoryMock = mockk<WorkflowRepository>()
    val nodeRepositoryMock = mockk<NodeRepository>()
    val edgeRepositoryMock = mockk<EdgeRepository>()
    val cut = WorkflowService(
        workflowRepository = workflowRepositoryMock,
        nodeRepository = nodeRepositoryMock,
        edgeRepository = edgeRepositoryMock
    )

    context("create") {
        should(
            "throw WorkflowAlreadyExistsException when given workflow already present in database"
        ) {
            // given
            val workflowCreate = WorkflowCreateGenerators.generator.next()

            every {
                workflowRepositoryMock.existsByNameAndVersion(
                    workflowCreate.name,
                    workflowCreate.version
                )
            } returns true

            // when
            val exception = shouldThrow<WorkflowAlreadyExistsException> {
                cut.create(workflowCreate)
            }

            // then
            exception.message shouldBe
                "Workflow with name '${workflowCreate.name}' and version '${workflowCreate.version}' already exists"
        }

        should(
            "throw WorkflowLatestVersionIsGreaterException when new workflow version smaller than latest version"
        ) {
            // given
            val workflowCreate = WorkflowCreateGenerators.generator.next()

            every {
                workflowRepositoryMock.existsByNameAndVersion(
                    workflowCreate.name,
                    workflowCreate.version
                )
            } returns false

            every { workflowRepositoryMock.findLatestVersion(workflowCreate.name) } returns workflowCreate.version + 1

            // when
            val exception = shouldThrow<WorkflowLatestVersionIsGreaterException> {
                cut.create(workflowCreate)
            }

            // then
            exception.message shouldBe "The latest version '${workflowCreate.version + 1}' of the workflow " +
                "'${workflowCreate.name}' is greater than the requested version '${workflowCreate.version}'"
        }
    }
})
