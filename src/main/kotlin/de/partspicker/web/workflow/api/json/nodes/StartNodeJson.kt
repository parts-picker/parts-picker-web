package de.partspicker.web.workflow.api.json.nodes

import com.fasterxml.jackson.annotation.JsonTypeName
import de.partspicker.web.workflow.api.json.enums.StartTypeJson

@JsonTypeName("start")
class StartNodeJson(
    name: String,
    displayName: String,
    val startType: StartTypeJson
) : NodeJson(name, displayName) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StartNodeJson) return false
        if (!super.equals(other)) return false

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
        return "StartNodeJson(name='$name', displayName='$displayName', startType=$startType)"
    }
}
