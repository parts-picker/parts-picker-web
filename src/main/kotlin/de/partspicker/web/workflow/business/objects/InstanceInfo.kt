package de.partspicker.web.workflow.business.objects

import de.partspicker.web.workflow.business.exceptions.WorkflowException
import de.partspicker.web.workflow.business.objects.enums.DisplayTypeInfo
import de.partspicker.web.workflow.persistence.entities.EdgeEntity
import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import de.partspicker.web.workflow.persistence.entities.nodes.AutomatedActionNodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.StartNodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.StopNodeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.UserActionNodeEntity

data class InstanceInfo(
    val nodeId: Long,
    val name: String,
    val displayName: String,
    val displayType: DisplayTypeInfo,
    val message: String?,
    val options: Set<EdgeInfo>,
    val instanceId: Long
) {
    companion object {
        fun from(
            nodeEntity: NodeEntity,
            instanceEntity: InstanceEntity,
            edgeEntities: Iterable<EdgeEntity>
        ): InstanceInfo {
            return when (nodeEntity) {
                is UserActionNodeEntity -> InstanceInfo(
                    nodeId = nodeEntity.id,
                    name = nodeEntity.name,
                    displayName = nodeEntity.displayName,
                    displayType = DisplayTypeInfo.from(instanceEntity.displayType),
                    message = instanceEntity.message,
                    options = EdgeInfo.AsSet.from(edgeEntities, instanceEntity.id),
                    instanceId = instanceEntity.id
                )

                is AutomatedActionNodeEntity -> InstanceInfo(
                    nodeId = nodeEntity.id,
                    name = nodeEntity.name,
                    displayName = nodeEntity.displayName,
                    displayType = DisplayTypeInfo.from(instanceEntity.displayType),
                    message = instanceEntity.message,
                    options = emptySet(),
                    instanceId = instanceEntity.id
                )

                is StartNodeEntity -> InstanceInfo(
                    nodeId = nodeEntity.id,
                    name = nodeEntity.name,
                    displayName = nodeEntity.displayName,
                    displayType = DisplayTypeInfo.from(instanceEntity.displayType),
                    message = instanceEntity.message,
                    options = EdgeInfo.AsSet.from(edgeEntities, instanceEntity.id),
                    instanceId = instanceEntity.id
                )

                is StopNodeEntity -> InstanceInfo(
                    nodeId = nodeEntity.id,
                    name = nodeEntity.name,
                    displayName = nodeEntity.displayName,
                    displayType = DisplayTypeInfo.from(instanceEntity.displayType),
                    message = instanceEntity.message,
                    options = emptySet(),
                    instanceId = instanceEntity.id
                )

                else -> throw WorkflowException() // use is branch w/ explicit null for child classes
            }
        }
    }
}
