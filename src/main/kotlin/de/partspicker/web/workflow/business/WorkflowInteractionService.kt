package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeSourceNotMatchingException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotActiveException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowStartedWithNonStartNodeException
import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.business.objects.InstanceInfo
import de.partspicker.web.workflow.persistance.EdgeRepository
import de.partspicker.web.workflow.persistance.InstanceRepository
import de.partspicker.web.workflow.persistance.NodeRepository
import de.partspicker.web.workflow.persistance.WorkflowRepository
import de.partspicker.web.workflow.persistance.entities.InstanceEntity
import de.partspicker.web.workflow.persistance.entities.nodes.NodeEntity
import de.partspicker.web.workflow.persistance.entities.nodes.StartNodeEntity
import de.partspicker.web.workflow.persistance.entities.nodes.StopNodeEntity
import jakarta.transaction.Transactional
import org.hibernate.Hibernate
import org.springframework.stereotype.Service

@Service
class WorkflowInteractionService(
    private val workflowRepository: WorkflowRepository,
    private val instanceRepository: InstanceRepository,
    private val nodeRepository: NodeRepository,
    private val edgeRepository: EdgeRepository,
    private val instanceValueService: InstanceValueService
) {
    companion object {
        const val PROJECT_WORKFLOW_NAME = "project_workflow"
        const val PROJECT_WORKFLOW_START_NODE = "new_project_start"
    }

    fun readInstanceInfo(instanceId: Long): InstanceInfo? {
        val instanceEntity = this.instanceRepository.findById(
            instanceId
        ).orElseThrow { WorkflowInstanceNotFoundException(instanceId) }

        val currentNodeEntity = instanceEntity.currentNode
            ?: return null
        val options = this.findOptionsBySourceNodeId(currentNodeEntity.id)

        return InstanceInfo.from(currentNodeEntity, instanceId, options)
    }

    fun startProjectWorkflow(instanceValues: Map<String, Any>? = null) = startWorkflowInstance(
        workflowName = PROJECT_WORKFLOW_NAME,
        startNodeName = PROJECT_WORKFLOW_START_NODE,
        instanceValues = instanceValues
    )

    @Transactional(rollbackOn = [Exception::class])
    fun startWorkflowInstance(
        workflowName: String,
        startNodeName: String,
        instanceValues: Map<String, Any>? = null
    ): Instance {
        val latestVersion = this.workflowRepository.findLatestVersion(workflowName)
            ?: throw WorkflowNameNotFoundException(workflowName)

        // if latestVersion exists -> value is present in db
        val workflow = this.workflowRepository.findByNameAndVersion(workflowName, latestVersion).get()

        val chosenStartNode = this.nodeRepository.findByWorkflowIdAndName(workflow.id, startNodeName)
            ?: throw WorkflowNodeNameNotFoundException(workflowName, startNodeName)

        if (chosenStartNode !is StartNodeEntity) {
            throw WorkflowStartedWithNonStartNodeException(workflowName, startNodeName)
        }

        // fetch first successor of start node
        val successor = this.edgeRepository.readBySourceId(chosenStartNode.id).target

        // create new instance
        val newInstance = InstanceEntity(
            id = 0,
            workflow = workflow,
            currentNode = Hibernate.unproxy(successor) as NodeEntity,
            active = true
        )

        val savedInstance = this.instanceRepository.save(newInstance)

        // save instance values
        instanceValues?.let {
            this.instanceValueService.setMultipleForInstance(savedInstance.id, it)
        }

        return Instance.from(savedInstance)
    }

    @Transactional(rollbackOn = [Exception::class])
    fun advanceInstanceNodeThroughEdge(
        instanceId: Long,
        edgeId: Long,
        values: Map<String, Any>? = null
    ): InstanceInfo? {
        val instanceEntity = this.instanceRepository.findById(instanceId)
            .orElseThrow { WorkflowInstanceNotFoundException(instanceId) }

        val currentNode = instanceEntity.currentNode
        if (currentNode == null || !instanceEntity.active) {
            throw WorkflowInstanceNotActiveException(instanceId)
        }

        val currentNodeId = currentNode.id
        val edge = this.edgeRepository.findById(edgeId)
            .orElseThrow { WorkflowEdgeNotFoundException(edgeId) }

        if (edge.source.id != currentNodeId) {
            throw WorkflowEdgeSourceNotMatchingException(edgeId, edge.source.id, currentNodeId)
        }

        // update instance values
        values?.let {
            this.instanceValueService.setMultipleForInstance(instanceId, it)
        }

        // check if target is stop node
        if (edge.target is StopNodeEntity) {
            instanceEntity.active = false
        }

        instanceEntity.currentNode = edge.target

        this.instanceRepository.save(instanceEntity)

        val options = this.findOptionsBySourceNodeId(edge.target.id)

        return InstanceInfo.from(Hibernate.unproxy(edge.target) as NodeEntity, instanceId, options)
    }

    private fun findOptionsBySourceNodeId(sourceNodeId: Long) = this.edgeRepository.findAllBySourceId(sourceNodeId)
}
