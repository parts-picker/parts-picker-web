package de.partspicker.web.test.builders

import de.partspicker.web.workflow.business.objects.create.EdgeCreate
import de.partspicker.web.workflow.business.objects.create.WorkflowCreate
import de.partspicker.web.workflow.business.objects.create.nodes.NodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StartNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StopNodeCreate

class WorkflowCreateBuilder(startNodeCreate: StartNodeCreate) {
    private var nodes: MutableList<NodeCreate> = mutableListOf()

    init {
        nodes.add(startNodeCreate)
    }

    private var name = "default_name"
    private var version = 1L

    fun append(nodeCreate: NodeCreate) = apply {
        require(nodeCreate !is StartNodeCreate && nodeCreate !is StopNodeCreate)

        nodes.add(nodeCreate)
    }

    fun withName(name: String) = apply { this.name = name }

    fun withVersion(version: Long) = apply { this.version = version }

    fun reset() = apply {
        nodes = mutableListOf()
        name = "default_name"
        version = 1L
    }

    fun build(stopNodeCreate: StopNodeCreate): WorkflowCreate {
        nodes.add(stopNodeCreate)

        val edgeCreates = nodes.zipWithNext().map { (current, next) ->
            val edgeName = "${current.name}-->${next.name}"
            EdgeCreate(name = edgeName, displayName = edgeName, sourceNode = current.name, targetNode = next.name)
        }

        val workflowCreate = WorkflowCreate(
            name = name,
            version = version,
            nodes = nodes,
            edges = edgeCreates,
        )

        this.reset()

        return workflowCreate
    }
}
