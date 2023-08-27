package de.partspicker.web.workflow.business.objects.create.nodes

import de.partspicker.web.workflow.business.objects.create.enums.StartTypeCreate

class StartNodeCreate(
    name: String,
    displayName: String,
    val startType: StartTypeCreate
) : NodeCreate(name, displayName) {
    init {
        check(displayName.isNotBlank())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StartNodeCreate) return false

        if (displayName != other.displayName) return false
        if (startType != other.startType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = displayName.hashCode()
        result = 31 * result + startType.hashCode()
        return result
    }

    override fun toString(): String {
        return "StartNodeCreate(name='$name', displayName='$displayName', startType='$startType')"
    }
}
