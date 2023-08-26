package de.partspicker.web.workflow.business

import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.workflow.business.automated.actions.AutomatedAction
import de.partspicker.web.workflow.business.exceptions.WorkflowAlreadyExistsException
import de.partspicker.web.workflow.business.exceptions.WorkflowAutomatedActionsMissingException
import de.partspicker.web.workflow.business.objects.Workflow
import de.partspicker.web.workflow.business.objects.create.WorkflowCreate
import de.partspicker.web.workflow.business.objects.create.nodes.AutomatedActionNodeCreate
import de.partspicker.web.workflow.persistence.EdgeRepository
import de.partspicker.web.workflow.persistence.NodeRepository
import de.partspicker.web.workflow.persistence.WorkflowRepository
import de.partspicker.web.workflow.persistence.entities.EdgeEntity
import de.partspicker.web.workflow.persistence.entities.WorkflowEntity
import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WorkflowService(
    private val workflowRepository: WorkflowRepository,
    private val nodeRepository: NodeRepository,
    private val edgeRepository: EdgeRepository,
    private val workflowMigrationService: WorkflowMigrationService,
    private val applicationContext: ApplicationContext
) {
    fun exists(name: String, version: Long) = this.workflowRepository.existsByNameAndVersion(name, version)

    fun readLatest(name: String): Workflow? {
        val latestWorkflowEntity = this.workflowRepository.findLatest(name) ?: return null

        return this.convertToWorkflow(latestWorkflowEntity)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun create(workflowCreate: WorkflowCreate): Workflow {
        if (this.exists(workflowCreate.name, workflowCreate.version)) {
            throw WorkflowAlreadyExistsException(workflowCreate.name, workflowCreate.version)
        }

        // convert & save workflow entity
        val newWorkflowEntity = WorkflowEntity.from(workflowCreate)
        val savedWorkflowEntity = this.workflowRepository.save(newWorkflowEntity)

        // check if all automated node's automatedActionNames are valid
        val automatedActionNames = applicationContext.getBeanNamesForType(AutomatedAction::class.java)
        val missingActionNames = workflowCreate.nodes
            .filterIsInstance<AutomatedActionNodeCreate>()
            .filter { !automatedActionNames.contains(it.automatedActionName) }
            .map { it.automatedActionName }

        missingActionNames.isEmpty() elseThrow WorkflowAutomatedActionsMissingException(
            workflowName = workflowCreate.name,
            actionNames = missingActionNames
        )

        // convert & save node entities & node lookup map
        val newNodeEntities = NodeEntity.AsList.from(workflowCreate.nodes, savedWorkflowEntity)
        val savedNodeEntities = this.nodeRepository.saveAll(newNodeEntities)
        val nodeLookupMap = savedNodeEntities.associateBy { it.name }

        // convert & save edge entities
        val newEdgeEntities = EdgeEntity.AsList.from(workflowCreate.edges, savedWorkflowEntity, nodeLookupMap)
        val savedEdgeEntities = this.edgeRepository.saveAll(newEdgeEntities)

        // create migration plan
        if (workflowCreate.latestWorkflow?.id != null && workflowCreate.defaultMigrationPlan != null) {
            this.workflowMigrationService.create(
                migrationPlanCreate = workflowCreate.defaultMigrationPlan,
                targetWorkflowId = savedWorkflowEntity.id,
                sourceWorkflowId = workflowCreate.latestWorkflow.id
            )
        }

        return Workflow.from(
            workflowEntity = savedWorkflowEntity,
            nodeEntities = savedNodeEntities,
            edgeEntities = savedEdgeEntities
        )
    }

    private fun convertToWorkflow(workflowEntity: WorkflowEntity): Workflow {
        val nodeEntities = this.nodeRepository.findAllByWorkflowId(workflowEntity.id)
        val edgeEntities = this.edgeRepository.findAllByWorkflowId(workflowEntity.id)

        return Workflow.from(workflowEntity, nodeEntities, edgeEntities)
    }
}
