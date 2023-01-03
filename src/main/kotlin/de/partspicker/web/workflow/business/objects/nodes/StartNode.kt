package de.partspicker.web.workflow.business.objects.nodes

import de.partspicker.web.workflow.business.objects.enums.StartType

class StartNode(
    id: Long,
    workflowId: Long,
    name: String,
    val displayName: String,
    val startType: StartType
) : Node(id, workflowId, name) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as StartNode

        if (displayName != other.displayName) return false
        if (startType != other.startType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + startType.hashCode()
        return result
    }

    override fun toString(): String {
        return "StartNode(displayName='$displayName', startType=$startType)"
    }
}
