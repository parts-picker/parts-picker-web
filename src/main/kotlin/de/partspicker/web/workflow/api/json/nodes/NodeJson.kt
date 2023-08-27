package de.partspicker.web.workflow.api.json.nodes

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
sealed class NodeJson(
    val name: String,
    val displayName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NodeJson) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
