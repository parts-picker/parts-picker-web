package de.partspicker.web.workflow.business.objects.create

import de.partspicker.web.workflow.api.json.EdgeJson

data class EdgeCreate(
    val name: String,
    val displayName: String,
    val sourceNode: String,
    val targetNode: String,
    val conditions: List<String> = emptyList()
) {
    init {
        require(name.isNotBlank())
        require(displayName.isNotBlank())
        require(sourceNode.isNotBlank())
        require(targetNode.isNotBlank())

        conditions.forEach {
            require(it.isNotBlank())
        }
    }

    companion object {
        fun from(edgeJson: EdgeJson) = EdgeCreate(
            name = edgeJson.name,
            displayName = edgeJson.displayName,
            sourceNode = edgeJson.sourceNode,
            targetNode = edgeJson.targetNode,
            conditions = edgeJson.conditions ?: emptyList()
        )
    }

    object AsList {
        fun from(edgeJsons: Iterable<EdgeJson>) = edgeJsons.map { from(it) }
    }
}
