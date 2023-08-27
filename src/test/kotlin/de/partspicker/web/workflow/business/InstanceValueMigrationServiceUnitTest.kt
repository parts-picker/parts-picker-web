package de.partspicker.web.workflow.business

import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.test.generators.workflow.WorkflowEntityGenerators
import de.partspicker.web.workflow.business.exceptions.ProjectWorkflowInstanceHasNoProjectException
import de.partspicker.web.workflow.business.objects.InstanceValue
import de.partspicker.web.workflow.business.objects.enums.InstanceValueType
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistence.InstanceValueMigrationRepository
import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import de.partspicker.web.workflow.persistence.entities.migration.InstanceValueMigrationEntity
import de.partspicker.web.workflow.persistence.entities.migration.enums.InstanceValueTypeMigrationEntity
import de.partspicker.web.workflow.persistence.entities.migration.enums.SupportedDataTypeMigrationEntity
import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.mockk
import org.springframework.expression.Expression
import org.springframework.expression.ExpressionParser

class InstanceValueMigrationServiceUnitTest : ShouldSpec({
    val instanceValueMigrationRepositoryMock = mockk<InstanceValueMigrationRepository>()
    val projectRepositoryMock = mockk<ProjectRepository>()
    val expressionParserMock = mockk<ExpressionParser>()
    val cut = InstanceValueMigrationService(
        instanceValueMigrationRepository = instanceValueMigrationRepositoryMock,
        projectRepository = projectRepositoryMock,
        expressionParser = expressionParserMock
    )

    context("convertToInstanceValues") {
        should("return a list of instance value based on the instance value migration rule") {
            // given
            val nodeEntityMock = mockk<NodeEntity>()
            every { nodeEntityMock.name } returns "some node name"

            val workflowEntity = WorkflowEntityGenerators.generator.single()
            every { nodeEntityMock.workflow } returns workflowEntity

            val instanceEntity = InstanceEntity(
                id = 1L,
                currentNode = nodeEntityMock,
                active = true
            )

            val instanceValueMigrationEntity = InstanceValueMigrationEntity(
                id = 1L,
                key = "key",
                value = "42",
                dataType = SupportedDataTypeMigrationEntity.LONG,
                valueType = InstanceValueTypeMigrationEntity.WORKFLOW,
                nodeMigration = mockk()
            )
            every { instanceValueMigrationRepositoryMock.findAllByNodeMigrationId(any()) } returns
                listOf(instanceValueMigrationEntity)

            val expressionMock = mockk<Expression>()
            every { expressionMock.getValue(any(), String::class.java) } returns instanceValueMigrationEntity.value
            every { expressionParserMock.parseExpression(any()) } returns expressionMock

            // when
            val result = cut.convertToInstanceValues(1L, instanceEntity)

            // then
            result shouldHaveSize 1
            result shouldContainOnly listOf(
                InstanceValue(
                    key = instanceValueMigrationEntity.key,
                    value = instanceValueMigrationEntity.value,
                    dataType = SupportedDataType.from(instanceValueMigrationEntity.dataType),
                    valueType = InstanceValueType.from(instanceValueMigrationEntity.valueType)
                )
            )
        }

        should("throw Exception when workflow is project_workflow & no project found") {
            // given
            val nodeEntityMock = mockk<NodeEntity>()
            every { nodeEntityMock.name } returns "some node name"

            val instanceEntity = InstanceEntity(
                id = 1L,
                currentNode = nodeEntityMock,
                active = true
            )
            every { projectRepositoryMock.findByWorkflowInstanceId(any()) } returns null

            val workflowEntity = WorkflowEntityGenerators.generator.single().copy(name = "project_workflow")
            every { nodeEntityMock.workflow } returns workflowEntity

            // when
            val exception = shouldThrow<ProjectWorkflowInstanceHasNoProjectException> {
                cut.convertToInstanceValues(1L, instanceEntity)
            }

            // then
            exception.message shouldBe "The instance with the given id ${instanceEntity.id} is not assigned to a " +
                "project but its workflow is 'project_workflow'"
        }
    }
})
