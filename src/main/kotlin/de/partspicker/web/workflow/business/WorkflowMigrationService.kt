package de.partspicker.web.workflow.business

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNotFoundException
import de.partspicker.web.workflow.business.exceptions.migration.WorkflowMigrationMissingNodeRuleException
import de.partspicker.web.workflow.business.objects.InstanceInfo
import de.partspicker.web.workflow.business.objects.InstanceValue
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
import de.partspicker.web.workflow.persistence.entities.migration.InstanceValueMigrationEntity
import de.partspicker.web.workflow.persistence.entities.migration.MigrationPlanEntity
import de.partspicker.web.workflow.persistence.entities.migration.NodeMigrationEntity
import de.partspicker.web.workflow.persistence.entities.migration.enums.InstanceValueTypeMigrationEntity
import de.partspicker.web.workflow.persistence.entities.migration.enums.SupportedDataTypeMigrationEntity
import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity
import org.hibernate.Hibernate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Suppress("LongParameterList")
class WorkflowMigrationService(
    private val migrationPlanRepository: MigrationPlanRepository,
    private val nodeMigrationRepository: NodeMigrationRepository,
    private val workflowRepository: WorkflowRepository,
    private val nodeRepository: NodeRepository,
    private val edgeRepository: EdgeRepository,
    private val instanceValueService: InstanceValueService,
    private val instanceRepository: InstanceRepository,
    private val instanceValueMigrationRepository: InstanceValueMigrationRepository,
    private val instanceValueMigrationService: InstanceValueMigrationService
) {
    companion object : LoggingUtil {
        val logger = logger()

        private fun logMigrationStartForAll() {
            logger.info("Attempting to migrate instances of outdated workflows to their current versions")
        }

        private fun logMigrationFinishedForAll(totalAmountMigrated: Long) {
            logger.info("Migration was completed successfully for $totalAmountMigrated instances")
        }

        private fun logMigrationStartForWorkflowWithName(
            migratedAmount: Long,
            workflowName: String
        ) {
            if (migratedAmount > 0) {
                logger.info("Completed migration for $migratedAmount instances of workflow named '$workflowName'")
            }
        }

        private fun logApplyMigrationPlanResult(
            migrationAmount: Long,
            previousVersion: WorkflowEntity,
            newerVersion: WorkflowEntity
        ) {
            if (migrationAmount > 0) {
                logger.info(
                    "${previousVersion.name}: V${previousVersion.version} -- $migrationAmount" +
                        " --> ${newerVersion.name}: V${newerVersion.version}"
                )
            }
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    @Suppress("ThrowsCount")
    fun create(
        migrationPlanCreate: MigrationPlanCreate,
        targetWorkflowId: Long,
        sourceWorkflowId: Long
    ) {
        if (!this.workflowRepository.existsById(targetWorkflowId)) {
            throw WorkflowNotFoundException(targetWorkflowId)
        }

        if (!this.workflowRepository.existsById(sourceWorkflowId)) {
            throw WorkflowNotFoundException(sourceWorkflowId)
        }

        val targetWorkflow = this.workflowRepository.getReferenceById(targetWorkflowId)
        val sourceWorkflow = this.workflowRepository.getReferenceById(sourceWorkflowId)
        val savedMigrationPlanEntity = migrationPlanRepository.save(
            MigrationPlanEntity.from(
                sourceWorkflowEntity = sourceWorkflow,
                targetWorkflowEntity = targetWorkflow
            )
        )

        // create nodeMigrations & instanceValueMigrations
        migrationPlanCreate.nodeMigrations.forEach { nodeMigrationCreate ->
            val sourceNodeEntity = this.nodeRepository.findByWorkflowIdAndName(
                sourceWorkflow.id,
                nodeMigrationCreate.sourceNode
            ) ?: throw WorkflowNodeNameNotFoundException(sourceWorkflow.name, nodeMigrationCreate.sourceNode)

            val targetNodeEntity = this.nodeRepository.findByWorkflowIdAndName(
                targetWorkflow.id,
                nodeMigrationCreate.targetNode
            ) ?: throw WorkflowNodeNameNotFoundException(targetWorkflow.name, nodeMigrationCreate.targetNode)

            val savedNodeMigrationEntity = this.nodeMigrationRepository.save(
                NodeMigrationEntity(
                    id = 0L,
                    source = sourceNodeEntity,
                    target = targetNodeEntity,
                    migrationPlan = savedMigrationPlanEntity
                )
            )

            val newInstanceValueMigrationEntities = nodeMigrationCreate.instanceValueMigrations.map {
                InstanceValueMigrationEntity(
                    id = 0L,
                    key = it.key,
                    value = it.value,
                    dataType = SupportedDataTypeMigrationEntity.from(it.dataType),
                    valueType = InstanceValueTypeMigrationEntity.from(it.valueType),
                    nodeMigration = savedNodeMigrationEntity
                )
            }
            this.instanceValueMigrationRepository.saveAll(newInstanceValueMigrationEntities)
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun migrateAllToLatestVersion(): Map<String, Long> {
        logMigrationStartForAll()

        val migratedAmount = this.workflowRepository
            .findAllWorkflowNames()
            .associateWith { this.migrateAllToLatestVersion(it) }

        logMigrationFinishedForAll(migratedAmount.values.sum())

        return migratedAmount
    }

    @Transactional(rollbackFor = [Exception::class])
    fun migrateAllToLatestVersion(workflowName: String): Long {
        val migratedAmount = this.workflowRepository.findAllByNameOrderByVersionAsc(workflowName)
            .zipWithNext()
            .sumOf { (previousVersion, newVersion) ->
                applyMigrationPlan(previousVersion, newVersion)
            }

        logMigrationStartForWorkflowWithName(migratedAmount, workflowName)

        return migratedAmount
    }

    /**
     * This method should only be used for administration and testing.
     */
    fun forceSetInstanceNodeWithinWorkflow(
        instanceId: Long,
        nodeName: String,
        values: List<InstanceValue>? = null,
    ): InstanceInfo {
        val instanceEntity = this.instanceRepository.findById(instanceId)
            .orElseThrow { WorkflowInstanceNotFoundException(instanceId) }

        val workflow = instanceEntity.workflow!!
        val targetNodeEntity = this.nodeRepository.findByWorkflowIdAndName(workflow.id, nodeName)
            ?: throw WorkflowNodeNameNotFoundException(workflow.name, nodeName)

        val savedInstanceEntity = this.instanceRepository.save(
            forceSetInstanceNode(instanceEntity, targetNodeEntity, values)
        )

        val options = this.edgeRepository.findAllBySourceId(savedInstanceEntity.currentNode!!.id)
        return InstanceInfo.from(
            Hibernate.unproxy(savedInstanceEntity.currentNode!!) as NodeEntity,
            savedInstanceEntity,
            options
        )
    }

    /**
     * This method should only be used for version migration.
     */
    private fun forceSetInstanceNode(
        instanceEntity: InstanceEntity,
        targetNodeEntity: NodeEntity,
        values: List<InstanceValue>?
    ): InstanceEntity {
        // update instance values
        values?.let {
            this.instanceValueService.setMultipleForInstance(instanceEntity.id, it)
        }

        instanceEntity.currentNode = targetNodeEntity
        instanceEntity.workflow = targetNodeEntity.workflow

        return instanceEntity
    }

    private fun applyMigrationPlan(previousVersion: WorkflowEntity, newVersion: WorkflowEntity): Long {
        val migrationPlanEntity = this.migrationPlanRepository
            .findBySourceWorkflowIdAndTargetWorkflowId(previousVersion.id, newVersion.id)
            ?: return 0L

        val nodeMigrationEntities = this.nodeMigrationRepository
            .findAllByMigrationPlanId(migrationPlanEntity.id)
            .associateBy { it.source.name }

        val migratedInstanceEntities = this.instanceRepository.findAllByWorkflowId(previousVersion.id)
            .map { instanceEntity ->
                val currentNode = instanceEntity.currentNode

                if (currentNode != null) {
                    val nodeMigrationRuleEntity = nodeMigrationEntities[currentNode.name]
                        ?: throw WorkflowMigrationMissingNodeRuleException(migrationPlanEntity.id, currentNode.name)

                    val instanceValuesToCreate = this.instanceValueMigrationService.convertToInstanceValues(
                        nodeMigrationRuleId = nodeMigrationRuleEntity.id,
                        instanceEntity = instanceEntity
                    )

                    this.forceSetInstanceNode(
                        instanceEntity = instanceEntity,
                        targetNodeEntity = nodeMigrationRuleEntity.target,
                        values = instanceValuesToCreate
                    )
                } else {
                    instanceEntity.workflow = newVersion
                    instanceEntity
                }
            }
        instanceRepository.saveAll(migratedInstanceEntities)

        val migratedAmountForVersion = migratedInstanceEntities.size.toLong()
        logApplyMigrationPlanResult(migratedAmountForVersion, previousVersion, newVersion)

        return migratedAmountForVersion
    }
}
