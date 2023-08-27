package de.partspicker.web.workflow.business

import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeSourceNotMatchingException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowStartedWithNonStartNodeException
import de.partspicker.web.workflow.business.objects.Edge
import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.business.objects.InstanceInfo
import de.partspicker.web.workflow.business.objects.InstanceValue
import de.partspicker.web.workflow.business.objects.enums.DisplayType
import de.partspicker.web.workflow.business.objects.nodes.Node
import de.partspicker.web.workflow.business.rules.InstanceActiveRule
import de.partspicker.web.workflow.business.rules.UserMayAdvanceNodeRule
import de.partspicker.web.workflow.persistence.EdgeRepository
import de.partspicker.web.workflow.persistence.InstanceRepository
import de.partspicker.web.workflow.persistence.NodeRepository
import de.partspicker.web.workflow.persistence.WorkflowRepository
import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import de.partspicker.web.workflow.persistence.entities.enums.DisplayTypeEntity
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

    fun readInstanceInfo(instanceId: Long): InstanceInfo {
        val instanceEntity = this.instanceRepository.findById(
            instanceId,
        ).orElseThrow { WorkflowInstanceNotFoundException(instanceId) }

        val currentNodeEntity = instanceEntity.currentNode
        val options = this.findOptionsBySourceNodeId(currentNodeEntity.id)

        return InstanceInfo.from(currentNodeEntity, instanceEntity, options)
    }

    @Transactional(readOnly = true)
    fun readProjectStatus(projectId: Long): String {
        val projectEntity = this.projectRepository.findById(projectId)
            .orElseThrow { ProjectNotFoundException(projectId) }

        return projectEntity.workflowInstance.currentNode.name
    }

    fun readEdgesBySourceNodeId(sourceNodeId: Long): Set<Edge> {
        val options = this.findOptionsBySourceNodeId(sourceNodeId = sourceNodeId)

        return Edge.AsSet.from(options)
    }

    fun startProjectWorkflow(): Instance {
        return startWorkflowInstance(
            workflowName = PROJECT_WORKFLOW_NAME,
            startNodeName = PROJECT_WORKFLOW_START_NODE,
        )
    }

    @Transactional(rollbackFor = [Exception::class])
    fun startWorkflowInstance(
        workflowName: String,
        startNodeName: String,
        values: List<InstanceValue>? = null,
        message: String? = null,
        displayType: DisplayType = DisplayType.DEFAULT
    ): Instance {
        val workflow = this.workflowRepository.findLatest(workflowName)
            ?: throw WorkflowNameNotFoundException(workflowName)

        val chosenStartNode = this.nodeRepository.findByWorkflowIdAndName(workflow.id, startNodeName)
            ?: throw WorkflowNodeNameNotFoundException(workflowName, startNodeName)

        if (chosenStartNode !is StartNodeEntity) {
            throw WorkflowStartedWithNonStartNodeException(workflowName, startNodeName)
        }

        // fetch first successor of start node
        val successor = Hibernate.unproxy(this.edgeRepository.readBySourceId(chosenStartNode.id).target) as NodeEntity

        // create new instance
        val newInstanceEntity = InstanceEntity(
            id = 0,
            workflow = workflow,
            currentNode = successor,
            active = true,
            message = message,
            displayType = DisplayTypeEntity.from(displayType)
        )

        val savedInstance = this.instanceRepository.save(newInstanceEntity)

        // save instance values
        values?.let {
            this.instanceValueService.setMultipleForInstance(savedInstance.id, it)
        }

        return Instance.from(savedInstance)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun advanceInstanceNodeByUser(
        instanceId: Long,
        edgeId: Long,
        values: List<InstanceValue>? = null,
    ): InstanceInfo {
        val instanceEntity = this.instanceRepository.findById(instanceId)
            .orElseThrow { WorkflowInstanceNotFoundException(instanceId) }

        UserMayAdvanceNodeRule(Node.from(instanceEntity.currentNode)).valid()

        return this.advanceInstanceNodeBySystem(
            instanceEntity = instanceEntity,
            edgeId = edgeId,
            values = values
        )
    }

    @Transactional(rollbackFor = [Exception::class])
    fun advanceInstanceNodeBySystem(
        instanceId: Long,
        edgeId: Long,
        values: List<InstanceValue>? = null,
        message: String? = null,
        displayType: DisplayType = DisplayType.DEFAULT
    ): InstanceInfo {
        val instanceEntity = this.instanceRepository.findById(instanceId)
            .orElseThrow { WorkflowInstanceNotFoundException(instanceId) }

        return this.advanceInstanceNodeBySystem(
            instanceEntity = instanceEntity,
            edgeId = edgeId,
            values = values,
            message = message,
            displayType = displayType
        )
    }

    @Transactional(rollbackFor = [Exception::class])
    fun advanceInstanceNodeBySystem(
        instanceEntity: InstanceEntity,
        edgeId: Long,
        values: List<InstanceValue>? = null,
        message: String? = null,
        displayType: DisplayType = DisplayType.DEFAULT
    ): InstanceInfo {
        InstanceActiveRule(Instance.fromOrNull(instanceEntity)).valid()

        val currentNodeId = instanceEntity.currentNode.id
        val edge = this.edgeRepository.findById(edgeId)
            .orElseThrow { WorkflowEdgeNotFoundException(edgeId) }

        if (edge.source.id != currentNodeId) {
            throw WorkflowEdgeSourceNotMatchingException(edgeId, edge.source.id, currentNodeId)
        }

        // update instance values
        values?.let {
            this.instanceValueService.setMultipleForInstance(instanceEntity.id, it)
        }

        // check if target is stop node
        if (edge.target is StopNodeEntity) {
            instanceEntity.active = false
        }

        instanceEntity.currentNode = edge.target

        instanceEntity.message = message
        instanceEntity.displayType = DisplayTypeEntity.from(displayType)

        val updatedInstanceEntity = this.instanceRepository.save(instanceEntity)

        val options = this.findOptionsBySourceNodeId(edge.target.id)

        return InstanceInfo.from(Hibernate.unproxy(edge.target) as NodeEntity, updatedInstanceEntity, options)
    }

    private fun findOptionsBySourceNodeId(sourceNodeId: Long) = this.edgeRepository.findAllBySourceId(sourceNodeId)
}
