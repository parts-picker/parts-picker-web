package de.partspicker.web.test.util

import de.partspicker.web.test.generators.workflow.WorkflowCreateGenerators
import de.partspicker.web.workflow.business.WorkflowInteractionService
import de.partspicker.web.workflow.business.WorkflowMigrationService
import de.partspicker.web.workflow.business.WorkflowService
import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.business.objects.InstanceInfo
import de.partspicker.web.workflow.business.objects.Workflow
import de.partspicker.web.workflow.business.objects.create.WorkflowCreate
import de.partspicker.web.workflow.business.objects.nodes.StartNode
import de.partspicker.web.workflow.persistence.InstanceRepository
import io.kotest.property.arbitrary.single
import org.springframework.stereotype.Component

@Component
class WorkflowTestSetupHelper(
    private val workflowService: WorkflowService,
    private val workflowInteractionService: WorkflowInteractionService,
    private val workflowMigrationService: WorkflowMigrationService,
    private val instanceRepository: InstanceRepository,
) {
    fun setupWorkflow(workflowCreate: WorkflowCreate = WorkflowCreateGenerators.generator.single()): Workflow {
        return workflowService.create(workflowCreate)
    }

    fun setupInstance(workflowName: String, startNodeName: String): Instance {
        return this.workflowInteractionService.startWorkflowInstance(workflowName, startNodeName)
    }

    /**
     * Starts an instance for the newest workflow with the given name in the first start node that can be found.
     */
    fun setupInstance(workflow: Workflow): Instance {
        val startNodeName = workflow.nodes.find { it is StartNode }?.name
            ?: error("Workflow must have a start node")

        return this.setupInstance(workflow.name, startNodeName)
    }

    fun forceInstanceStatus(instanceId: Long, targetNodeName: String): InstanceInfo {
        return this.workflowMigrationService.forceSetInstanceNodeWithinWorkflow(instanceId, targetNodeName)
    }

    fun readInstanceEntity(instanceId: Long) = this.instanceRepository.getReferenceById(instanceId)
}
