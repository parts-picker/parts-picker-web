package de.partspicker.web.workflow.business

import de.partspicker.web.test.generators.workflow.InstanceEntityGenerators
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNotFoundException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationMissingNodeRuleException
import de.partspicker.web.workflow.business.objects.create.migration.MigrationPlanCreate
import de.partspicker.web.workflow.persistence.EdgeRepository
import de.partspicker.web.workflow.persistence.InstanceRepository
import de.partspicker.web.workflow.persistence.InstanceValueMigrationRepository
import de.partspicker.web.workflow.persistence.MigrationPlanRepository
import de.partspicker.web.workflow.persistence.NodeMigrationRepository
import de.partspicker.web.workflow.persistence.NodeRepository
import de.partspicker.web.workflow.persistence.WorkflowRepository
import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import de.partspicker.web.workflow.persistence.entities.WorkflowEntity
import de.partspicker.web.workflow.persistence.entities.enums.DisplayTypeEntity
import de.partspicker.web.workflow.persistence.entities.migration.MigrationPlanEntity
import de.partspicker.web.workflow.persistence.entities.nodes.UserActionNodeEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.util.Optional

class WorkflowMigrationServiceUnitTest : ShouldSpec({
    val migrationPlanRepositoryMock = mockk<MigrationPlanRepository>()
    val nodeMigrationRepositoryMock = mockk<NodeMigrationRepository>()
    val workflowRepositoryMock = mockk<WorkflowRepository>()
    val nodeRepositoryMock = mockk<NodeRepository>()
    val edgeRepositoryMock = mockk<EdgeRepository>()
    val instanceValueServiceMock = mockk<InstanceValueService>()
    val instanceRepositoryMock = mockk<InstanceRepository>()
    val instanceValueMigrationRepositoryMock = mockk<InstanceValueMigrationRepository>()
    val instanceValueMigrationServiceMock = mockk<InstanceValueMigrationService>()
    val cut = WorkflowMigrationService(
        migrationPlanRepository = migrationPlanRepositoryMock,
        nodeMigrationRepository = nodeMigrationRepositoryMock,
        workflowRepository = workflowRepositoryMock,
        nodeRepository = nodeRepositoryMock,
        edgeRepository = edgeRepositoryMock,
        instanceValueService = instanceValueServiceMock,
        instanceRepository = instanceRepositoryMock,
        instanceValueMigrationRepository = instanceValueMigrationRepositoryMock,
        instanceValueMigrationService = instanceValueMigrationServiceMock
    )

    context("create") {
        should("throw WorkflowNotFoundException when given non existent target workflow id") {
            // given
            val targetWorkflowId = 1L
            every { workflowRepositoryMock.existsById(targetWorkflowId) } returns false
            val migrationPlanCreateMock = mockk<MigrationPlanCreate>()

            // when
            val exception = shouldThrow<WorkflowNotFoundException> {
                cut.create(migrationPlanCreateMock, targetWorkflowId, 2L)
            }

            // then
            exception.message shouldBe "Workflow with id '$targetWorkflowId' could not be found"
        }

        should("throw WorkflowNotFoundException when given non existent source workflow id") {
            // given
            val targetWorkflowId = 1L
            every { workflowRepositoryMock.existsById(targetWorkflowId) } returns true
            val sourceWorkflowId = 2L
            every { workflowRepositoryMock.existsById(sourceWorkflowId) } returns false

            val migrationPlanCreateMock = mockk<MigrationPlanCreate>()

            // when
            val exception = shouldThrow<WorkflowNotFoundException> {
                cut.create(migrationPlanCreateMock, targetWorkflowId, sourceWorkflowId)
            }

            // then
            exception.message shouldBe "Workflow with id '$sourceWorkflowId' could not be found"
        }
    }

    context("migrateAllToLatestVersion") {
        should("call migrateAllToLatestVersion(name) for each available workflow name") {
            // given
            val names = setOf("firstFlow", "secondFlow", "thirdFlow")
            every { workflowRepositoryMock.findAllWorkflowNames() } returns names

            val cutSpy = spyk(cut)
            every { cutSpy.migrateAllToLatestVersion(any()) } returns 1L

            // when
            val migratedAmount = cutSpy.migrateAllToLatestVersion()

            // then
            migratedAmount.forAll {
                it.key shouldBeOneOf names
                it.value shouldBe 1L
            }

            verify(exactly = names.size) { cutSpy.migrateAllToLatestVersion(any()) }
        }
    }

    context("migrateAllToLatestVersion(workflowName)") {
        should("throw WorkflowMigrationMissingNodeRuleException when node rule is missing") {
            // given
            val sourceWorkflowEntity = WorkflowEntity(
                id = 1L,
                name = "testflow",
                version = 1L
            )
            val targetWorkflowEntity = WorkflowEntity(
                id = 2L,
                name = sourceWorkflowEntity.name,
                version = sourceWorkflowEntity.version + 1
            )

            every {
                workflowRepositoryMock.findAllByNameOrderByVersionAsc(any())
            } returns listOf(sourceWorkflowEntity, targetWorkflowEntity)

            val migrationPlanEntity = MigrationPlanEntity(
                id = 1L,
                targetWorkflow = targetWorkflowEntity,
                sourceWorkflow = sourceWorkflowEntity
            )
            every {
                migrationPlanRepositoryMock.findBySourceWorkflowIdAndTargetWorkflowId(
                    sourceWorkflowId = sourceWorkflowEntity.id,
                    targetWorkflowId = targetWorkflowEntity.id
                )
            } returns migrationPlanEntity

            every { nodeMigrationRepositoryMock.findAllByMigrationPlanId(migrationPlanEntity.id) } returns emptyList()

            val currentNodeEntity = UserActionNodeEntity(
                id = 2L,
                workflow = sourceWorkflowEntity,
                name = "action",
                displayName = ""
            )
            val instanceEntity = InstanceEntity(
                id = 1L,
                currentNode = currentNodeEntity,
                message = null,
                displayType = DisplayTypeEntity.DEFAULT
            )
            every { instanceRepositoryMock.findAllByCurrentNodeWorkflowId(sourceWorkflowEntity.id) } returns
                listOf(instanceEntity)

            // when
            val exception = shouldThrow<WorkflowMigrationMissingNodeRuleException> {
                cut.migrateAllToLatestVersion(sourceWorkflowEntity.name)
            }

            // then
            exception.message shouldBe "The given migration plan with id ${migrationPlanEntity.id} is " +
                "missing a rule for the source node with name ${currentNodeEntity.name}"
        }
    }

    context("forceSetInstanceNodeWithinWorkflow") {
        should("throw WorkflowInstanceNotFoundException when given non-existing instance id") {
            // given
            val nonExistentId = 666L
            val nodeName = "implementation"

            every { instanceRepositoryMock.findById(nonExistentId) } returns Optional.empty()

            // when & then
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.forceSetInstanceNodeWithinWorkflow(
                    nonExistentId,
                    nodeName
                )
            }

            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }

        should("throw WorkflowNodeNameNotFoundException when given non-existing node name") {
            // given
            val instanceId = 1L
            val nonExistentNodeName = "non-existent node name"

            val instanceEntity = InstanceEntityGenerators.generator.single()
            every { instanceRepositoryMock.findById(instanceId) } returns Optional.of(instanceEntity)
            every { nodeRepositoryMock.findByWorkflowIdAndName(any(), any()) } returns null

            // when & then
            val exception = shouldThrow<WorkflowNodeNameNotFoundException> {
                cut.forceSetInstanceNodeWithinWorkflow(
                    instanceId,
                    nonExistentNodeName
                )
            }

            exception.message shouldBe
                "Workflow node with name $nonExistentNodeName could not be found for workflow" +
                " with name ${instanceEntity.currentNode.workflow.name}"
        }
    }
})
