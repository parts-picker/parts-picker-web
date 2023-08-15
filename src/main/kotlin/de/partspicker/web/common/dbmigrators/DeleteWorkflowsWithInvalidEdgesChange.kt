package de.partspicker.web.common.dbmigrators

import liquibase.change.custom.CustomTaskChange
import liquibase.database.Database
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.CustomChangeException
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor

/**
 * CustomChange used by PICK-106 to delete workflows with invalid nodes with multiple edges.
 */
class DeleteWorkflowsWithInvalidEdgesChange : CustomTaskChange {
    companion object {
        const val SELECT_NODES_WITH_MULTIPLE_EDGES = "SELECT workflow_id, source_node_id FROM workflow_edges " +
            "GROUP BY workflow_id, source_node_id HAVING COUNT(id) > 1"

        const val PREPARED_SELECT_NODE_TYPE_BY_NODE_ID =
            "SELECT node_type FROM workflow_nodes WHERE id = ?"

        const val PREPARED_SELECT_WORKFLOW = "SELECT name, version FROM workflows WHERE id = ?"

        const val PREPARED_DELETE_WORKFLOW = "DELETE FROM workflows WHERE id = ?"
    }

    private val deletedWorkflowIds = mutableSetOf<Long>()

    override fun getConfirmationMessage(): String {
        return "${deletedWorkflowIds.size} workflow(s) with invalid edges was/were deleted"
    }

    @Suppress("EmptyFunctionBlock")
    override fun setUp() {
    }

    @Suppress("EmptyFunctionBlock")
    override fun setFileOpener(resourceAccessor: ResourceAccessor?) {
    }

    override fun validate(database: Database): ValidationErrors {
        return ValidationErrors()
    }

    override fun execute(database: Database) {
        val connection = database.connection as JdbcConnection
        val statement = connection.createStatement()

        // check if any single-edge nodes have multiple edges
        val nodesWithMultipleEdgesResult = statement.executeQuery(SELECT_NODES_WITH_MULTIPLE_EDGES)

        // prepare statements
        val preparedSelectNodeTypeStatement = connection.prepareStatement(PREPARED_SELECT_NODE_TYPE_BY_NODE_ID)
        val preparedSelectWorkflowStatement = connection.prepareStatement(PREPARED_SELECT_WORKFLOW)
        val preparedDeleteWorkflowStatement = connection.prepareStatement(PREPARED_DELETE_WORKFLOW)

        // check node type != user_action, else delete workflow & children
        while (nodesWithMultipleEdgesResult.next()) {
            val workflowIdToDelete = nodesWithMultipleEdgesResult.getLong("workflow_id")
            if (deletedWorkflowIds.contains(workflowIdToDelete)) {
                continue
            }

            val nodeId = nodesWithMultipleEdgesResult.getLong("source_node_id")

            preparedSelectNodeTypeStatement.setLong(1, nodeId)
            val nodeTypeResult = preparedSelectNodeTypeStatement.executeQuery()
            nodeTypeResult.next()
            val nodeType = nodeTypeResult.getString("node_type")

            if (nodeType != "user_action") {
                // delete workflow
                preparedSelectWorkflowStatement.setLong(1, workflowIdToDelete)
                val workflowResult = preparedSelectWorkflowStatement.executeQuery()
                workflowResult.next()

                val name = workflowResult.getString("name")
                if (name == "project_workflow") {
                    throw CustomChangeException(
                        "Invalid project workflow with id $workflowIdToDelete with nodes with multiple edges\n" +
                            "Node with id $nodeId & type '$nodeType' has multiple edges but may only have one\n" +
                            "This needs to be fixed manually"
                    )
                }

                preparedDeleteWorkflowStatement.setLong(1, workflowIdToDelete)
                preparedDeleteWorkflowStatement.executeUpdate()
                deletedWorkflowIds.add(workflowIdToDelete)
            }
        }
    }
}
