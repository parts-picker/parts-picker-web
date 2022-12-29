package de.partspicker.web.workflow.business.objects.create.nodes

import de.partspicker.web.workflow.api.json.nodes.NodeJson
import de.partspicker.web.workflow.api.json.nodes.StartNodeJson
import de.partspicker.web.workflow.api.json.nodes.StopNodeJson
import de.partspicker.web.workflow.api.json.nodes.UserActionNodeJson
import de.partspicker.web.workflow.business.objects.create.enums.StartTypeCreate

sealed class NodeCreate(
    val name: String
) {
    init {
        require(name.isNotBlank())
    }

    companion object {
        fun from(nodeJson: NodeJson): NodeCreate = when (nodeJson) {
            is UserActionNodeJson -> UserActionNodeCreate(
                name = nodeJson.name,
                displayName = nodeJson.displayName
            )

            is StartNodeJson -> StartNodeCreate(
                name = nodeJson.name,
                displayName = nodeJson.displayName,
                startType = StartTypeCreate.from(nodeJson.startType)
            )

            is StopNodeJson -> StopNodeCreate(
                name = nodeJson.name,
                displayName = nodeJson.displayName
            )
        }
    }

    object AsList {
        fun from(nodeJsons: Iterable<NodeJson>) = nodeJsons.map { from(it) }
    }
}
