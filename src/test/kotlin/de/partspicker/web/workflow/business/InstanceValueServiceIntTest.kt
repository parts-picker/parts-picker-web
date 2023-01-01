package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistance.InstanceValueRepository
import de.partspicker.web.workflow.persistance.entities.enums.InstanceValueTypeEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
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
            val values: Map<String, Pair<Any, SupportedDataType>> = mapOf(
                "numberValue" to (9 to SupportedDataType.LONG),
                "stringValue" to ("value" to SupportedDataType.STRING)
            )

            val instanceId = 1L

            // when
            instanceValueService.setMultipleForInstance(instanceId, values)

            // then
            val valuesToCheck = instanceValueRepository.findAllByWorkflowInstanceIdAndType(
                instanceId,
                InstanceValueTypeEntity.WORKFLOW
            ).map { it.key }

            valuesToCheck shouldHaveSize 2
            valuesToCheck shouldContain "numberValue"
            valuesToCheck shouldContain "stringValue"
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
                1L,
                key,
                value,
                SupportedDataType.LONG
            )

            // then
            savedValue.first shouldBe value.toString()
            savedValue.second shouldBe SupportedDataType.LONG
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent instance") {
            val nonExistentId = 666L
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                instanceValueService.setForInstance(nonExistentId, "key", "value", SupportedDataType.STRING)
            }

            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }
    }
})
