package de.partspicker.web.workflow.business.objects.create.nodes

import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.workflow.api.json.nodes.AutomatedActionNodeJson
import de.partspicker.web.workflow.api.json.nodes.NodeJson
import de.partspicker.web.workflow.api.json.nodes.StartNodeJson
import de.partspicker.web.workflow.api.json.nodes.StopNodeJson
import de.partspicker.web.workflow.api.json.nodes.UserActionNodeJson
import de.partspicker.web.workflow.business.exceptions.WorkflowIllegalStateException
import de.partspicker.web.workflow.business.objects.create.enums.StartTypeCreate

sealed class NodeCreate(
    val name: String,
    val displayName: String
) {
    init {
        name.isNotBlank() elseThrow WorkflowIllegalStateException(NAME_IS_BLANK)
    }

    companion object {
        const val NAME_IS_BLANK = "Name must not be blank"

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

            is AutomatedActionNodeJson -> AutomatedActionNodeCreate(
                name = nodeJson.name,
                displayName = nodeJson.displayName,
                automatedActionName = nodeJson.automatedActionName
            )
        }
    }

    object AsList {
        fun from(nodeJsons: Iterable<NodeJson>) = nodeJsons.map { from(it) }
    }
}
