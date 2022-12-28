package de.partspicker.web.workflow.api.json

data class EdgeJson(
    val name: String,
    val displayName: String,
    val sourceNode: String,
    val targetNode: String,
    val conditions: List<String>?
)
