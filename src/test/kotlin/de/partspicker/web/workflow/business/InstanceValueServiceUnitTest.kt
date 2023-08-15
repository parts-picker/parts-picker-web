package de.partspicker.web.workflow.business

import de.partspicker.web.test.generators.workflow.InstanceValueCreateGenerators
import de.partspicker.web.workflow.business.exceptions.UnsupportedDataTypeException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistence.InstanceRepository
import de.partspicker.web.workflow.persistence.InstanceValueRepository
import de.partspicker.web.workflow.persistence.entities.InstanceValueEntity
import de.partspicker.web.workflow.persistence.entities.enums.InstanceValueTypeEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class InstanceValueServiceUnitTest : ShouldSpec({
    val instanceValueRepositoryMock = mockk<InstanceValueRepository>()
    val instanceRepositoryMock = mockk<InstanceRepository>()
    val cut = InstanceValueService(
        instanceValueRepository = instanceValueRepositoryMock,
        instanceRepository = instanceRepositoryMock
    )

    context("setMultipleForInstance") {
        should("overwrite value when given value with existing key") {
            // given
            every { instanceRepositoryMock.existsById(any()) } returns true

            val existingKey = "existing"
            val values = listOf(InstanceValueCreateGenerators.generator.single().copy(key = existingKey))

            val instanceId = 100L
            val existingInstanceValueId = 5L
            val existingInstanceValueMock = mockk<InstanceValueEntity>()
            every { existingInstanceValueMock.id } returns existingInstanceValueId
            every {
                instanceValueRepositoryMock.findByWorkflowInstanceIdAndTypeAndKey(
                    instanceId,
                    InstanceValueTypeEntity.WORKFLOW,
                    existingKey
                )
            } returns existingInstanceValueMock
            every { instanceValueRepositoryMock.saveAll<InstanceValueEntity>(any()) } returns emptyList()

            // when
            cut.setMultipleForInstance(instanceId, values)

            // then
            verify {
                instanceValueRepositoryMock.saveAll<InstanceValueEntity>(
                    match {
                        it shouldHaveSize 1
                        it.first().id shouldBe existingInstanceValueId

                        true
                    }
                )
            }
        }

        should("throw WorkflowInstanceNotFoundException when given id with non-existent instance") {
            // given
            every { instanceRepositoryMock.existsById(any()) } returns false

            val values = listOf(
                InstanceValueCreateGenerators.generator.single(),
                InstanceValueCreateGenerators.generator.single()
            )
            val nonExistentId = 666L

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.setMultipleForInstance(nonExistentId, values)
            }

            // then
            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }
    }

    context("setMultipleWithAutoTypeDetectionForInstance") {
        should("throw WorkflowInstanceNotFoundException when given non-existent instance") {
            // given
            every { instanceRepositoryMock.existsById(any()) } returns false

            val nonExistentId = 666L

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.setMultipleWithAutoTypeDetectionForInstance(nonExistentId, emptyMap())
            }

            // then
            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }

        should("throw DatatypeNotSupportedException when given value with unsupported data type") {
            // given
            every { instanceRepositoryMock.existsById(any()) } returns true
            every {
                instanceValueRepositoryMock.findByWorkflowInstanceIdAndTypeAndKey(
                    any(),
                    any(),
                    any()
                )
            } returns null

            val values: Map<String, Any> = mapOf(
                "booleanValue" to true
            )

            val instanceId = 100L

            // when
            val exception = shouldThrow<UnsupportedDataTypeException> {
                cut.setMultipleWithAutoTypeDetectionForInstance(instanceId, values)
            }

            // then
            exception.message shouldBe "The given data with datatype 'Boolean' is not supported. " +
                "Supported types are ${SupportedDataType.values().map { it.name }}"
        }
    }

    context("setSingleWithAutoTypeDetectionForInstance") {
        should("throw DatatypeNotSupportedException when given value with unsupported data type") {
            // given
            every { instanceRepositoryMock.existsById(any()) } returns true
            every {
                instanceValueRepositoryMock.findByWorkflowInstanceIdAndTypeAndKey(
                    any(),
                    any(),
                    any()
                )
            } returns null

            val value = "booleanValue" to true

            val instanceId = 100L

            // when
            val exception = shouldThrow<UnsupportedDataTypeException> {
                cut.setSingleWithAutoTypeDetectionForInstance(instanceId, value.first, value.second)
            }

            // then
            exception.message shouldBe "The given data with datatype 'Boolean' is not supported. " +
                "Supported types are ${SupportedDataType.values().map { it.name }}"
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent instance") {
            // given
            every { instanceRepositoryMock.existsById(any()) } returns false

            val nonExistentId = 666L

            // when
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.setSingleWithAutoTypeDetectionForInstance(nonExistentId, "key", "value")
            }

            // then
            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }
    }
})
