package de.partspicker.web.workflow.api.json.nodes

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("automated_action")
class AutomatedActionNodeJson(
    name: String,
    val displayName: String,
    val automatedActionName: String
) : NodeJson(name = name) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as AutomatedActionNodeJson

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
        return "AutomatedActionNodeJson(name='$name', displayName='$displayName'," +
            " automatedActionName='$automatedActionName')"
    }
}
