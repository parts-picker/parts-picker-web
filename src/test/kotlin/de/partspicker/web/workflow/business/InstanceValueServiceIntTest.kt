package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.DatatypeNotSupportedException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistance.InstanceValueRepository
import de.partspicker.web.workflow.persistance.entities.enums.InstanceValueTypeEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/instanceValueServiceIntTest.sql")
class InstanceValueServiceIntTest(
    private val instanceValueService: InstanceValueService,
    private val instanceValueRepository: InstanceValueRepository
) : ShouldSpec({
    context("setForMultiple") {
        should("save all given values") {
            // given
            val values: Map<String, Any> = mapOf(
                "numberValue" to 9L,
                "stringValue" to "value"
            )

            val instanceId = 100L

            // when
            instanceValueService.setMultipleForInstance(instanceId, values)

            // then
            val valuesToCheck = instanceValueRepository.findAllByWorkflowInstanceIdAndType(
                instanceId,
                InstanceValueTypeEntity.WORKFLOW
            ).map { it.key }

            valuesToCheck shouldHaveSize 3
            valuesToCheck shouldContain "numberValue"
            valuesToCheck shouldContain "stringValue"
        }

        should("throw DatatypeNotSupportedException when given value with unsupported data type") {
            // given
            val values: Map<String, Any> = mapOf(
                "booleanValue" to true
            )

            val instanceId = 100L

            // when
            val exception = shouldThrow<DatatypeNotSupportedException> {
                instanceValueService.setMultipleForInstance(instanceId, values)
            }

            // then
            exception.message shouldBe "The given data with datatype 'Boolean' is not supported. " +
                "Supported types are ${SupportedDataType.values().map { it.name }}"
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent instance") {
            val nonExistentId = 666L
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                instanceValueService.setMultipleForInstance(nonExistentId, emptyMap())
            }

            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }
    }

    context("set") {
        should("save given value") {
            // give
            val key = "numberValue"
            val value = 2L
            // when
            val savedValue = instanceValueService.setForInstance(
                100L,
                key,
                value
            )

            // then
            savedValue.first shouldBe value.toString()
            savedValue.second shouldBe SupportedDataType.LONG
        }

        should("overwrite existing value") {
            // give
            val key = "existing"
            val value = "newValue"
            // when
            val savedValue = instanceValueService.setForInstance(
                100L,
                key,
                value
            )

            // then
            savedValue.first shouldBe value
            savedValue.second shouldBe SupportedDataType.STRING
        }

        should("throw DatatypeNotSupportedException when given value with unsupported data type") {
            // given
            val value = "booleanValue" to true

            val instanceId = 100L

            // when
            val exception = shouldThrow<DatatypeNotSupportedException> {
                instanceValueService.setForInstance(instanceId, value.first, value.second)
            }

            // then
            exception.message shouldBe "The given data with datatype 'Boolean' is not supported. " +
                "Supported types are ${SupportedDataType.values().map { it.name }}"
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent instance") {
            val nonExistentId = 666L
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                instanceValueService.setForInstance(nonExistentId, "key", "value")
            }

            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
