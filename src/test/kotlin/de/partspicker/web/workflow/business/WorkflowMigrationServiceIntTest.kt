package de.partspicker.web.workflow.business

import de.partspicker.web.test.annotations.ReducedSpringTestContext
import de.partspicker.web.test.builders.WorkflowJsonBuilder
import de.partspicker.web.workflow.api.json.migration.ExplicitMigrationPlanJson
import de.partspicker.web.workflow.api.json.migration.InstanceValueMigrationJson
import de.partspicker.web.workflow.api.json.migration.NodeMigrationJson
import de.partspicker.web.workflow.api.json.migration.enums.SupportedDataTypeJson
import de.partspicker.web.workflow.business.objects.create.InstanceValueCreate
import de.partspicker.web.workflow.business.objects.create.WorkflowCreate
import de.partspicker.web.workflow.business.objects.create.enums.SupportedDataTypeCreate
import de.partspicker.web.workflow.business.objects.create.migration.MigrationPlanCreate
import de.partspicker.web.workflow.persistence.InstanceRepository
import de.partspicker.web.workflow.persistence.MigrationPlanRepository
import de.partspicker.web.workflow.persistence.NodeMigrationRepository
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.context.annotation.Import

@ReducedSpringTestContext
@Import(
    InstanceValueReadService::class,
    WorkflowService::class,
    WorkflowMigrationService::class,
    InstanceValueService::class,
    WorkflowInteractionService::class
)
@Suppress("LongParameterList")
class WorkflowMigrationServiceIntTest(
    private val cut: WorkflowMigrationService,
    // support classes
    private val instanceValueReadService: InstanceValueReadService,
    private val workflowService: WorkflowService,
    private val migrationPlanRepository: MigrationPlanRepository,
    private val nodeMigrationRepository: NodeMigrationRepository,
    private val workflowInteractionService: WorkflowInteractionService,
    private val instanceRepository: InstanceRepository,
) : ShouldSpec({
    context("create") {
        should("create the given migration plan") {
            // given
            // setup workflows
            val sourceWorkflowCreate = WorkflowJsonBuilder()
                .withName("test_flow")
                .withVersion(2L)
                .append("start", "stop")
                .buildAndConvertToCreate()
            val sourceWorkflow = workflowService.create(sourceWorkflowCreate)

            val targetWorkflowJson = WorkflowJsonBuilder()
                .withName(sourceWorkflowCreate.name)
                .withVersion(sourceWorkflowCreate.version + 1)
                .append("start", "node1", "stop")
                .buildJson()
            val targetWorkflow = workflowService.create(WorkflowCreate.from(targetWorkflowJson, null))

            val migrationPlanCreate = MigrationPlanCreate.from(
                targetWorkflowJson = targetWorkflowJson,
                sourceWorkflow = sourceWorkflow
            )

            // when
            cut.create(
                migrationPlanCreate = migrationPlanCreate,
                targetWorkflowId = targetWorkflow.id,
                sourceWorkflowId = sourceWorkflow.id
            )

            // then
            // check migration plan
            val createdMigrationPlan = migrationPlanRepository.findBySourceWorkflowIdAndTargetWorkflowId(
                sourceWorkflowId = sourceWorkflow.id,
                targetWorkflowId = targetWorkflow.id
            )

            createdMigrationPlan shouldNotBe null
            createdMigrationPlan!!.sourceWorkflow shouldNotBe null
            createdMigrationPlan.sourceWorkflow.id shouldBe sourceWorkflow.id
            createdMigrationPlan.targetWorkflow shouldNotBe null
            createdMigrationPlan.targetWorkflow.id shouldBe targetWorkflow.id

            // check node migrations
            val createdNodeMigrations = nodeMigrationRepository.findAllByMigrationPlanId(createdMigrationPlan.id)

            createdNodeMigrations shouldHaveSize 2
            createdNodeMigrations.forAll {
                it.migrationPlan.id shouldBe createdMigrationPlan.id
            }
        }
    }

    context("migrateAllToLatestVersion(workflowName)") {
        should("migrate all instances of workflows with the given name by implicit migration rules") {
            // given
            // create workflows
            val workflowName = "test_flow"
            val startNodeName = "start"
            val firstNodeName = "node1"

            val sourceWorkflowCreate = WorkflowJsonBuilder()
                .withName(workflowName)
                .withVersion(2L)
                .append(startNodeName, firstNodeName, "stop")
                .buildAndConvertToCreate()
            val createdSourceWorkflow = workflowService.create(sourceWorkflowCreate)

            val instance = workflowInteractionService.startWorkflowInstance(
                workflowName,
                startNodeName
            )

            val targetWorkflowCreate = WorkflowJsonBuilder()
                .withName(workflowName)
                .withVersion(3L)
                .append(startNodeName, firstNodeName, "node2", "stop")
                .buildAndConvertToCreate(latestWorkflow = createdSourceWorkflow)
            val createdTargetWorkflow = workflowService.create(targetWorkflowCreate)

            // when
            cut.migrateAllToLatestVersion(workflowName)

            // then
            val instanceEntity = instanceRepository.findById(instance.id)

            instanceEntity.isPresent shouldBe true
            instanceEntity.get().workflow!!.id shouldBe createdTargetWorkflow.id
            instanceEntity.get().currentNode!!.workflow.id shouldBe createdTargetWorkflow.id
            // node is moved from start to the first non-automatic node automatically
            instanceEntity.get().currentNode!!.name shouldBe firstNodeName
        }

        should("migrate all instances of workflows with the given name by implicit & explicit migration rules") {
            // given
            // create workflows
            val workflowName = "test_flow"
            val startNodeName = "start"
            val firstNodeName = "node1"
            val secondNodeName = "node2"

            val sourceWorkflowCreate = WorkflowJsonBuilder()
                .withName(workflowName)
                .withVersion(2L)
                .append(startNodeName, firstNodeName, "stop")
                .buildAndConvertToCreate()
            val createdSourceWorkflow = workflowService.create(sourceWorkflowCreate)

            val instance = workflowInteractionService.startWorkflowInstance(
                workflowName,
                startNodeName
            )

            val instanceValueKey = "KEY"
            val instanceValue = "9"
            val explicitMigrationPlanJson = ExplicitMigrationPlanJson(
                listOf(
                    NodeMigrationJson(
                        firstNodeName,
                        secondNodeName,
                        listOf(
                            InstanceValueMigrationJson(
                                instanceValueKey,
                                instanceValue,
                                SupportedDataTypeJson.INTEGER
                            )
                        )
                    )
                )
            )

            val targetWorkflowCreate = WorkflowJsonBuilder()
                .withName(workflowName)
                .withVersion(3L)
                .withExplicitMigrationPlanJson(explicitMigrationPlanJson)
                .append(startNodeName, firstNodeName, secondNodeName, "stop")
                .buildAndConvertToCreate(latestWorkflow = createdSourceWorkflow)
            val createdTargetWorkflow = workflowService.create(targetWorkflowCreate)

            // when
            cut.migrateAllToLatestVersion(workflowName)

            // then
            val instanceEntity = instanceRepository.findById(instance.id)

            instanceEntity.isPresent shouldBe true
            instanceEntity.get().workflow!!.id shouldBe createdTargetWorkflow.id
            instanceEntity.get().currentNode!!.workflow.id shouldBe createdTargetWorkflow.id
            // node is moved from start to the first non-automatic node automatically
            // & then migrated through explicit rule
            instanceEntity.get().currentNode!!.name shouldBe secondNodeName

            // check instance values
            val instanceValues = instanceValueReadService.readAllForInstance(instanceEntity.get().id)
            instanceValues shouldHaveSize 1
            instanceValues[instanceValueKey]!!.first shouldBe instanceValue
        }
    }

    context("forceSetInstanceNodeWithinWorkflow") {
        should("set instance to node with given name and instance values & return the updated instance info") {
            // given
            val nodeName = "implementation"
            val startNodeName = "start"

            // create workflow
            val workflowCreate = WorkflowJsonBuilder()
                .withName("test_flow")
                .withVersion(2L)
                .append(startNodeName, nodeName, "stop")
                .buildAndConvertToCreate()
            val createdWorkflow = workflowService.create(workflowCreate)

            // create instance & instance values
            val instanceValueToCreate = "key" to 9L
            val instance = workflowInteractionService.startWorkflowInstance(
                createdWorkflow.name,
                startNodeName,
                mapOf(instanceValueToCreate)
            )

            // when
            val instanceValueCreate = InstanceValueCreate(
                key = instanceValueToCreate.first,
                value = "someString",
                SupportedDataTypeCreate.STRING
            )
            val updatedInstance = cut.forceSetInstanceNodeWithinWorkflow(
                instance.id,
                nodeName,
                listOf(instanceValueCreate)
            )

            // then
            updatedInstance shouldNotBe null
            updatedInstance.instanceId shouldBe instance.id
            updatedInstance.name shouldBe nodeName
            updatedInstance.displayName shouldBe nodeName

            val instanceEntity = instanceRepository.findById(updatedInstance.instanceId)
            instanceEntity.isPresent shouldBe true
            instanceEntity.get().workflow!!.id shouldBe createdWorkflow.id

            val instanceValues = instanceValueReadService.readAllForInstance(instance.id)
            instanceValues shouldHaveSize 1
            instanceValues[instanceValueCreate.key]!!.first shouldBe instanceValueCreate.value
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
