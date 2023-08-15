package de.partspicker.web.test.builders

import de.partspicker.web.workflow.api.json.EdgeJson
import de.partspicker.web.workflow.api.json.WorkflowJson
import de.partspicker.web.workflow.api.json.enums.StartTypeJson
import de.partspicker.web.workflow.api.json.migration.ExplicitMigrationPlanJson
import de.partspicker.web.workflow.api.json.nodes.StartNodeJson
import de.partspicker.web.workflow.api.json.nodes.StopNodeJson
import de.partspicker.web.workflow.api.json.nodes.UserActionNodeJson
import de.partspicker.web.workflow.business.objects.Workflow
import de.partspicker.web.workflow.business.objects.create.WorkflowCreate

class WorkflowJsonBuilder {
    private val nodeNames = mutableListOf<String>()

    private var name = "default_name"
    private var version = 1L
    private var explicitMigrationPlanJson: ExplicitMigrationPlanJson? = null

    fun append(vararg names: String) = apply { nodeNames.addAll(names) }

    fun withName(name: String) = apply { this.name = name }

    fun withVersion(version: Long) = apply { this.version = version }

    fun withExplicitMigrationPlanJson(explicitMigrationPlanJson: ExplicitMigrationPlanJson) = apply {
        this.explicitMigrationPlanJson = explicitMigrationPlanJson
    }

    fun reset() = apply {
        nodeNames.clear()
        name = "default_name"
        version = 1L
        explicitMigrationPlanJson = null
    }

    fun buildJson(): WorkflowJson {
        val nodeJsons = nodeNames.mapIndexed { index, nodeName ->
            when (index) {
                0 -> StartNodeJson(name = nodeName, displayName = nodeName, startType = StartTypeJson.WORKFLOW)
                nodeNames.size - 1 -> StopNodeJson(name = nodeName, displayName = nodeName)
                else -> UserActionNodeJson(name = nodeName, displayName = nodeName)
            }
        }

        val egdeJsons = nodeNames.zipWithNext().map { (current, next) ->
            val edgeName = "$current-->$next"
            EdgeJson(name = edgeName, displayName = edgeName, sourceNode = current, targetNode = next)
        }

        val workflowJson = WorkflowJson(
            name = name,
            version = version,
            nodes = nodeJsons,
            edges = egdeJsons,
            migrationPlan = explicitMigrationPlanJson
        )

        this.reset()

        return workflowJson
    }

    fun buildAndConvertToCreate(latestWorkflow: Workflow? = null): WorkflowCreate {
        return WorkflowCreate.from(this.buildJson(), latestWorkflow)
    }
}
