package de.partspicker.web.workflow.business.objects

import de.partspicker.web.workflow.business.exceptions.WorkflowException
import de.partspicker.web.workflow.persistance.entities.nodes.NodeEntity
import de.partspicker.web.workflow.persistance.entities.nodes.StartNodeEntity
import de.partspicker.web.workflow.persistance.entities.nodes.UserActionNodeEntity

data class NodeInfo(
    val id: Long,
    val name: String,
    val displayName: String,
    val userCanInteract: Boolean,
    val instanceId: Long
) {
    companion object {
        fun from(nodeEntity: NodeEntity, instanceId: Long): NodeInfo? {
            return when (nodeEntity) {
                is UserActionNodeEntity -> NodeInfo(
                    id = nodeEntity.id,
                    name = nodeEntity.name,
                    displayName = nodeEntity.displayName,
                    userCanInteract = true,
                    instanceId = instanceId
                )
                is StartNodeEntity -> null
                else -> throw WorkflowException() // use is branch w/ explicit null for child classes
            }
        }
    }
}
