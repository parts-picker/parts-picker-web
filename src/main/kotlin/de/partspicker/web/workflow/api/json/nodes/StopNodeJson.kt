package de.partspicker.web.workflow.api.json.nodes

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("stop")
class StopNodeJson(
    name: String,
    displayName: String
) : NodeJson(name, displayName) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StopNodeJson) return false
        if (!super.equals(other)) return false

        if (displayName != other.displayName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + displayName.hashCode()
        return result
    }

    override fun toString(): String {
        return "StopNodeJson(name='$name', displayName='$displayName')"
    }
}
