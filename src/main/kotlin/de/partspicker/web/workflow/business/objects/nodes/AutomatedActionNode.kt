package de.partspicker.web.workflow.business.objects.nodes

class AutomatedActionNode(
    id: Long,
    workflowId: Long,
    name: String,
    displayName: String,
    val automatedActionName: String,
) : Node(id, workflowId, name, displayName) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as AutomatedActionNode

        if (displayName != other.displayName) return false
        if (automatedActionName != other.automatedActionName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + automatedActionName.hashCode()
        return result
    }

    override fun toString(): String {
        return "AutomatedActionNode(name='$name', displayName='$displayName'," +
            " automatedActionName='$automatedActionName')"
    }
}
