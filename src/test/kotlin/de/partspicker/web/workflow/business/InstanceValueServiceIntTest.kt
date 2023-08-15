package de.partspicker.web.workflow.business

import de.partspicker.web.test.generators.workflow.InstanceValueCreateGenerators
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistence.InstanceValueRepository
import de.partspicker.web.workflow.persistence.entities.enums.InstanceValueTypeEntity
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
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
    context("setMultipleForInstance") {
        should("save all given values") {
            // given
            val amount = 10
            val values = List(amount) { InstanceValueCreateGenerators.generator.single() }

            val instanceId = 101L

            // when
            instanceValueService.setMultipleForInstance(instanceId, values)

            // then
            val returnedValues = instanceValueRepository.findAllByWorkflowInstanceIdAndType(
                instanceId,
                InstanceValueTypeEntity.WORKFLOW
            )
            returnedValues shouldHaveSize amount
        }
    }

    context("setMultipleWithAutoTypeDetectionForInstance") {
        should("save all given values") {
            // given
            val values: Map<String, Any> = mapOf(
                "numberValue" to 9L,
                "stringValue" to "value"
            )

            val instanceId = 100L

            // when
            instanceValueService.setMultipleWithAutoTypeDetectionForInstance(instanceId, values)

            // then
            val valuesToCheck = instanceValueRepository.findAllByWorkflowInstanceIdAndType(
                instanceId,
                InstanceValueTypeEntity.WORKFLOW
            ).map { it.key }

            valuesToCheck shouldHaveSize 3
            valuesToCheck shouldContain "numberValue"
            valuesToCheck shouldContain "stringValue"
        }
    }

    context("setSingleWithAutoTypeDetectionForInstance") {
        should("save given value") {
            // give
            val key = "numberValue"
            val value = 2L
            // when
            val savedValue = instanceValueService.setSingleWithAutoTypeDetectionForInstance(
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
            val savedValue = instanceValueService.setSingleWithAutoTypeDetectionForInstance(
                100L,
                key,
                value
            )

            // then
            savedValue.first shouldBe value
            savedValue.second shouldBe SupportedDataType.STRING
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
