package de.partspicker.web.workflow.business

import de.partspicker.web.test.builders.WorkflowCreateBuilder
import de.partspicker.web.test.generators.workflow.NodeCreateGenerators
import de.partspicker.web.test.generators.workflow.WorkflowCreateGenerators
import de.partspicker.web.workflow.business.automated.actions.AutomatedAction
import de.partspicker.web.workflow.business.exceptions.WorkflowAlreadyExistsException
import de.partspicker.web.workflow.business.exceptions.WorkflowAutomatedActionsMissingException
import de.partspicker.web.workflow.business.objects.create.nodes.AutomatedActionNodeCreate
import de.partspicker.web.workflow.persistence.EdgeRepository
import de.partspicker.web.workflow.persistence.NodeRepository
import de.partspicker.web.workflow.persistence.WorkflowRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.mockk
import org.springframework.context.ApplicationContext

class WorkflowServiceUnitTest : ShouldSpec({

    val workflowRepositoryMock = mockk<WorkflowRepository>()
    val nodeRepositoryMock = mockk<NodeRepository>()
    val edgeRepositoryMock = mockk<EdgeRepository>()
    val workflowMigrationServiceMock = mockk<WorkflowMigrationService>()
    val applicationContextMock = mockk<ApplicationContext>()
    val cut = WorkflowService(
        workflowRepository = workflowRepositoryMock,
        nodeRepository = nodeRepositoryMock,
        edgeRepository = edgeRepositoryMock,
        workflowMigrationService = workflowMigrationServiceMock,
        applicationContext = applicationContextMock
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

        should("throw Exception when at least one automated action name not a bean in application context") {
            // given
            val automatedActionNodeCreate = AutomatedActionNodeCreate(
                name = "automatedAction",
                displayName = "not blank",
                automatedActionName = "not a valid name"
            )
            val workflowCreate = WorkflowCreateBuilder(NodeCreateGenerators.startNodeCreateGenerator.single())
                .withName("testflows")
                .withVersion(1L)
                .append(automatedActionNodeCreate)
                .build(NodeCreateGenerators.stopNodeCreateGenerator.single())

            every {
                workflowRepositoryMock.existsByNameAndVersion(
                    workflowCreate.name,
                    workflowCreate.version
                )
            } returns false

            every { workflowRepositoryMock.save(any()) } returnsArgument 0

            val automatedActionNames = arrayOf("bean1", "bean2")
            every {
                applicationContextMock.getBeanNamesForType(AutomatedAction::class.java)
            } returns automatedActionNames

            // when
            val exception = shouldThrow<WorkflowAutomatedActionsMissingException> {
                cut.create(workflowCreate)
            }

            // then
            exception.message shouldBe "The workflow named '${workflowCreate.name}' cannot be created as the " +
                "requested automated actions named '[${automatedActionNodeCreate.automatedActionName}]' are missing"
        }
    }
})
