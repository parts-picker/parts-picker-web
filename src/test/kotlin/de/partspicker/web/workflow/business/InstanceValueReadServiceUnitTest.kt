package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.persistence.InstanceRepository
import de.partspicker.web.workflow.persistence.InstanceValueRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class InstanceValueReadServiceUnitTest : ShouldSpec({
    val instanceValueRepositoryMock = mockk<InstanceValueRepository>()
    val instanceRepositoryMock = mockk<InstanceRepository>()
    val cut = InstanceValueReadService(
        instanceValueRepository = instanceValueRepositoryMock,
        instanceRepository = instanceRepositoryMock
    )

    context("readAllForInstance") {
        should("throw WorkflowInstanceNotFoundException when given non-existent instance") {
            every { instanceRepositoryMock.existsById(any()) } returns false

            val nonExistentId = 666L
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.readAllForInstance(nonExistentId)
            }

            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }
    }

    context("readForInstanceByKey") {
        should("throw WorkflowInstanceNotFoundException when given non-existent instance") {
            every { instanceRepositoryMock.existsById(any()) } returns false

            val nonExistentId = 666L
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                cut.readForInstanceByKey(nonExistentId, "someKey")
            }

            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }

        should("return null when the key does not exists for the given instance id") {
            every { instanceRepositoryMock.existsById(any()) } returns true
            every {
                instanceValueRepositoryMock.findByWorkflowInstanceIdAndTypeAndKey(
                    any(),
                    any(),
                    any()
                )
            } returns null

            val value = cut.readForInstanceByKey(100, "nonExistentKey")

            value shouldBe null
        }
    }
})
