package de.partspicker.web.workflow.business

import de.partspicker.web.test.generators.workflow.WorkflowCreateGenerators
import de.partspicker.web.workflow.business.exceptions.WorkflowAlreadyExistsException
import de.partspicker.web.workflow.persistence.EdgeRepository
import de.partspicker.web.workflow.persistence.NodeRepository
import de.partspicker.web.workflow.persistence.WorkflowRepository
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
    val workflowMigrationServiceMock = mockk<WorkflowMigrationService>()
    val cut = WorkflowService(
        workflowRepository = workflowRepositoryMock,
        nodeRepository = nodeRepositoryMock,
        edgeRepository = edgeRepositoryMock,
        workflowMigrationService = workflowMigrationServiceMock
    )

    context("create") {
        should("throw WorkflowAlreadyExistsException when given workflow already present in database") {
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
    }
})
