package de.partspicker.web.workflow.business.objects

import de.partspicker.web.workflow.business.exceptions.WorkflowException
import de.partspicker.web.workflow.persistance.entities.EdgeEntity
import de.partspicker.web.workflow.persistance.entities.nodes.NodeEntity
import de.partspicker.web.workflow.persistance.entities.nodes.StartNodeEntity
import de.partspicker.web.workflow.persistance.entities.nodes.StopNodeEntity
import de.partspicker.web.workflow.persistance.entities.nodes.UserActionNodeEntity

data class InstanceInfo(
    val id: Long,
    val name: String,
    val displayName: String,
    val instanceId: Long,
    val options: Set<EdgeInfo>
) {
    companion object {
        fun from(nodeEntity: NodeEntity, instanceId: Long, edgeEntities: Iterable<EdgeEntity>): InstanceInfo? {
            return when (nodeEntity) {
                is UserActionNodeEntity -> InstanceInfo(
                    id = nodeEntity.id,
                    name = nodeEntity.name,
                    displayName = nodeEntity.displayName,
                    instanceId = instanceId,
                    options = EdgeInfo.AsSet.from(edgeEntities, instanceId)
                )

                is StartNodeEntity -> InstanceInfo(
                    id = nodeEntity.id,
                    name = nodeEntity.name,
                    displayName = nodeEntity.displayName,
                    instanceId = instanceId,
                    options = EdgeInfo.AsSet.from(edgeEntities, instanceId)
                )

                is StopNodeEntity -> InstanceInfo(
                    id = nodeEntity.id,
                    name = nodeEntity.name,
                    displayName = nodeEntity.displayName,
                    instanceId = instanceId,
                    options = EdgeInfo.AsSet.from(edgeEntities, instanceId)
                )

                else -> throw WorkflowException() // use is branch w/ explicit null for child classes
            }
        }
    }
}
