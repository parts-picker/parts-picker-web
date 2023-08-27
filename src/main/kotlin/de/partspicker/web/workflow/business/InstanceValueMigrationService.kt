package de.partspicker.web.workflow.business

import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.workflow.business.WorkflowInteractionService.Companion.PROJECT_WORKFLOW_NAME
import de.partspicker.web.workflow.business.exceptions.ProjectWorkflowInstanceHasNoProjectException
import de.partspicker.web.workflow.business.objects.InstanceValue
import de.partspicker.web.workflow.business.objects.enums.InstanceValueType
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistence.InstanceValueMigrationRepository
import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Service

@Service
class InstanceValueMigrationService(
    private val instanceValueMigrationRepository: InstanceValueMigrationRepository,
    private val projectRepository: ProjectRepository,
    private val expressionParser: ExpressionParser
) {
    fun convertToInstanceValues(
        nodeMigrationRuleId: Long,
        instanceEntity: InstanceEntity
    ): List<InstanceValue> {
        // fetch projectId if instance of project workflow
        var projectId: Long? = null
        if (instanceEntity.currentNode.workflow.name == PROJECT_WORKFLOW_NAME) {
            projectId = this.projectRepository.findByWorkflowInstanceId(instanceId = instanceEntity.id)?.id
                ?: throw ProjectWorkflowInstanceHasNoProjectException(instanceEntity.id)
        }
        val instanceValueMigrationContext = InstanceValueMigrationContext.from(
            instanceEntity = instanceEntity,
            projectId = projectId
        )
        val evaluationContext = StandardEvaluationContext(instanceValueMigrationContext)

        return this.instanceValueMigrationRepository
            .findAllByNodeMigrationId(nodeMigrationRuleId)
            .map {
                val parsedValue = it.value?.let {
                        nonNullValue ->
                    expressionParser.parseExpression(nonNullValue).getValue(evaluationContext, String::class.java)
                }

                InstanceValue(
                    key = it.key,
                    value = parsedValue,
                    dataType = SupportedDataType.from(it.dataType),
                    valueType = InstanceValueType.from(it.valueType)
                )
            }
    }
}

data class InstanceValueMigrationContext(
    val instance: InstanceContext,
    val projectId: Long?
) {
    companion object {
        fun from(instanceEntity: InstanceEntity, projectId: Long?) = InstanceValueMigrationContext(
            instance = InstanceContext.from(instanceEntity),
            projectId = projectId
        )
    }
}

data class InstanceContext(
    val workflowName: String,
    val workflowVersion: Long,
    val status: String,
    val active: Boolean
) {
    companion object {
        fun from(instanceEntity: InstanceEntity) = InstanceContext(
            workflowName = instanceEntity.currentNode.workflow.name,
            workflowVersion = instanceEntity.currentNode.workflow.version,
            status = instanceEntity.currentNode.name,
            active = instanceEntity.active
        )
    }
}
