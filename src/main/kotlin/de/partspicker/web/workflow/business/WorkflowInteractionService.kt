package de.partspicker.web.workflow.business

import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeSourceNotMatchingException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotActiveException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowStartedWithNonStartNodeException
import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.business.objects.InstanceInfo
import de.partspicker.web.workflow.persistence.EdgeRepository
import de.partspicker.web.workflow.persistence.InstanceRepository
import de.partspicker.web.workflow.persistence.NodeRepository
import de.partspicker.web.workflow.persistence.WorkflowRepository
import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.StartNodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.StopNodeEntity
import org.hibernate.Hibernate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WorkflowInteractionService(
    private val workflowRepository: WorkflowRepository,
    private val instanceRepository: InstanceRepository,
    private val nodeRepository: NodeRepository,
    private val edgeRepository: EdgeRepository,
    private val instanceValueService: InstanceValueService,
    private val projectRepository: ProjectRepository,
) {
    companion object {
        const val PROJECT_WORKFLOW_NAME = "project_workflow"
        const val PROJECT_WORKFLOW_START_NODE = "new_project_start"
    }

    fun readInstanceInfo(instanceId: Long): InstanceInfo? {
        val instanceEntity = this.instanceRepository.findById(
            instanceId,
        ).orElseThrow { WorkflowInstanceNotFoundException(instanceId) }

        val currentNodeEntity = instanceEntity.currentNode
            ?: return null
        val options = this.findOptionsBySourceNodeId(currentNodeEntity.id)

        return InstanceInfo.from(currentNodeEntity, instanceId, options)
    }

    @Transactional(readOnly = true)
    fun readProjectStatus(projectId: Long): String? {
        val projectEntity = this.projectRepository.findById(projectId)
            .orElseThrow { ProjectNotFoundException(projectId) }

        return projectEntity.workflowInstance!!.currentNode?.name
    }

    fun startProjectWorkflow(instanceValues: Map<String, Any>? = null) = startWorkflowInstance(
        workflowName = PROJECT_WORKFLOW_NAME,
        startNodeName = PROJECT_WORKFLOW_START_NODE,
        instanceValues = instanceValues,
    )

    @Transactional(rollbackFor = [Exception::class])
    fun startWorkflowInstance(
        workflowName: String,
        startNodeName: String,
        instanceValues: Map<String, Any>? = null,
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
        val successor = Hibernate.unproxy(this.edgeRepository.readBySourceId(chosenStartNode.id).target) as NodeEntity

        // create new instance
        val newInstance = InstanceEntity(
            id = 0,
            workflow = workflow,
            currentNode = successor,
            active = true,
        )

        val savedInstance = this.instanceRepository.save(newInstance)

        // save instance values
        instanceValues?.let {
            this.instanceValueService.setMultipleWithAutoTypeDetectionForInstance(savedInstance.id, it)
        }

        return Instance.from(savedInstance)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun advanceInstanceNodeThroughEdge(
        instanceId: Long,
        edgeId: Long,
        values: Map<String, Any>? = null,
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
            this.instanceValueService.setMultipleWithAutoTypeDetectionForInstance(instanceId, it)
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
