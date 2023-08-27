package de.partspicker.web.workflow.business.objects.nodes

class StopNode(
    id: Long,
    workflowId: Long,
    name: String,
    displayName: String
) : Node(id, workflowId, name, displayName) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as StopNode

        if (displayName != other.displayName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + displayName.hashCode()
        return result
    }

    override fun toString(): String {
        return "StopNode(displayName='$displayName')"
    }
}
