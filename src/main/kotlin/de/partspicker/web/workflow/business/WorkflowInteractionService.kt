package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.InstanceNotFoundException
import de.partspicker.web.workflow.business.objects.EdgeInfo
import de.partspicker.web.workflow.business.objects.NodeInfo
import de.partspicker.web.workflow.persistance.EdgeRepository
import de.partspicker.web.workflow.persistance.InstanceRepository
import org.springframework.stereotype.Service

@Service
class WorkflowInteractionService(
    private val instanceRepository: InstanceRepository,
    private val edgeRepository: EdgeRepository
) {

    fun readCurrentNodeByInstanceId(instanceId: Long): NodeInfo? {
        val instanceEntity = this.instanceRepository.findById(
            instanceId
        ).orElseThrow { InstanceNotFoundException(instanceId) }

        val nodeEntity = instanceEntity.currentNode
            ?: return null

        return NodeInfo.from(nodeEntity, instanceId)
    }

    fun readPossibleEdgesByNodeId(nodeId: Long): Set<EdgeInfo> {
        val possibleEdges = this.edgeRepository.findAllBySourceId(nodeId)

        return EdgeInfo.AsSet.from(possibleEdges)
    }
}
