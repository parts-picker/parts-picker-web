package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowStartedWithNonStartNodeException
import de.partspicker.web.workflow.business.objects.EdgeInfo
import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.business.objects.NodeInfo
import de.partspicker.web.workflow.persistance.EdgeRepository
import de.partspicker.web.workflow.persistance.InstanceRepository
import de.partspicker.web.workflow.persistance.NodeRepository
import de.partspicker.web.workflow.persistance.WorkflowRepository
import de.partspicker.web.workflow.persistance.entities.InstanceEntity
import de.partspicker.web.workflow.persistance.entities.nodes.StartNodeEntity
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class WorkflowInteractionService(
    private val workflowRepository: WorkflowRepository,
    private val instanceRepository: InstanceRepository,
    private val nodeRepository: NodeRepository,
    private val edgeRepository: EdgeRepository,
    private val instanceValueService: InstanceValueService
) {

    fun readCurrentNodeByInstanceId(instanceId: Long): NodeInfo? {
        val instanceEntity = this.instanceRepository.findById(
            instanceId
        ).orElseThrow { WorkflowInstanceNotFoundException(instanceId) }

        val nodeEntity = instanceEntity.currentNode
            ?: return null

        return NodeInfo.from(nodeEntity, instanceId)
    }

    fun readPossibleEdgesByNodeId(nodeId: Long): Set<EdgeInfo> {
        val possibleEdges = this.edgeRepository.findAllBySourceId(nodeId)

        return EdgeInfo.AsSet.from(possibleEdges)
    }

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

        // create new instance
        val newInstance = InstanceEntity(
            id = 0,
            workflow = workflow,
            currentNode = chosenStartNode,
            active = true
        )

        val savedInstance = this.instanceRepository.save(newInstance)

        // save instance values
        instanceValues?.let {
            this.instanceValueService.setMultipleForInstance(savedInstance.id, it)
        }

        return Instance.from(savedInstance)
    }
}
