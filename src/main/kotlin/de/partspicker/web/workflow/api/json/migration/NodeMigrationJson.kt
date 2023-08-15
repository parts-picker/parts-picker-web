package de.partspicker.web.workflow.api.json.migration

data class NodeMigrationJson(
    val sourceNode: String,
    val targetNode: String,
    val instanceValueMigrations: List<InstanceValueMigrationJson>
)
