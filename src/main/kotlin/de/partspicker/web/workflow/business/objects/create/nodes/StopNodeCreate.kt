package de.partspicker.web.workflow.business.objects.create.nodes

class StopNodeCreate(
    name: String,
    displayName: String
) : NodeCreate(name, displayName) {
    init {
        check(displayName.isNotBlank())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StopNodeCreate) return false

        if (displayName != other.displayName) return false

        return true
    }

    override fun hashCode(): Int {
        return displayName.hashCode()
    }

    override fun toString(): String {
        return "StopNodeCreate(name='$name', displayName='$displayName')"
    }
}
