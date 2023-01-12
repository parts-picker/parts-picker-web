package de.partspicker.web.common.dbmigrators

import liquibase.change.custom.CustomTaskChange
import liquibase.database.Database
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor

class GenerateInstancesForProjectsChange : CustomTaskChange {
    companion object {
        const val SELECT_PROJECT_FALLBACK_WORKFLOW =
            "SELECT id FROM workflows WHERE name = 'project_workflow' AND version = 0"

        const val SELECT_PROJECTS_WITHOUT_INSTANCE =
            "SELECT id FROM projects WHERE instance_id IS null"

        const val PREPARED_SELECT_START_NODE_BY_WORFLOWID =
            "SELECT id FROM workflow_nodes WHERE workflow_id = ? AND node_type = 'start'"

        const val PREPARED_INSERT_INSTANCE =
            "INSERT INTO workflow_instances VALUES (nextval('instance_id_seq'), ?, ?, true)"

        const val PREPARED_UPDATE_PROJECT_WITH_INSTANCE_ID =
            "UPDATE projects SET instance_id = currval('instance_id_seq') WHERE id = ?"
    }

    private var projectsProcessed = 0

    override fun getConfirmationMessage(): String {
        return "Workflow instances were created for $projectsProcessed projects without an existing instance"
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

        // fetch id of fallback project workflow
        val workflowIdResult = statement.executeQuery(SELECT_PROJECT_FALLBACK_WORKFLOW)
        workflowIdResult.next()
        val workflowId = workflowIdResult.getLong("id")

        // find single start node for workflow
        val preparedSelectStartNodeStatement = connection.prepareStatement(PREPARED_SELECT_START_NODE_BY_WORFLOWID)
        preparedSelectStartNodeStatement.setLong(1, workflowId)
        val nodeIdResult = preparedSelectStartNodeStatement.executeQuery()
        nodeIdResult.next()
        val nodeId = nodeIdResult.getLong("id")

        // fetch all projects without an instance & create instances for each
        val preparedInsertInstanceStatement = connection.prepareStatement(PREPARED_INSERT_INSTANCE)
        val preparedUpdateProjectStatement = connection.prepareStatement(PREPARED_UPDATE_PROJECT_WITH_INSTANCE_ID)

        val projectsResult = statement.executeQuery(SELECT_PROJECTS_WITHOUT_INSTANCE)
        while (projectsResult.next()) {
            // create instance
            preparedInsertInstanceStatement.setLong(1, workflowId)
            preparedInsertInstanceStatement.setLong(2, nodeId)
            preparedInsertInstanceStatement.executeUpdate()

            // update instance_id of project
            val projectId = projectsResult.getLong("id")
            preparedUpdateProjectStatement.setLong(1, projectId)
            preparedUpdateProjectStatement.executeUpdate()

            this.projectsProcessed += 1
        }
    }
}
