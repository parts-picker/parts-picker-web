package de.partspicker.web.workflow.business.objects.nodes

class UserActionNode(
    id: Long,
    workflowId: Long,
    name: String,
    displayName: String
) : Node(id, workflowId, name, displayName) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserActionNode) return false
        if (!super.equals(other)) return false

        if (displayName != other.displayName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + displayName.hashCode()
        return result
    }
}
