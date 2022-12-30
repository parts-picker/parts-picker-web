package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.maps.shouldHaveKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/instanceValueReadServiceIntTest.sql")
class InstanceValueReadServiceIntTest(
    private val instanceValueReadService: InstanceValueReadService
) : ShouldSpec({

    context("readAll") {
        should("read all instance values belonging to the given id & return them") {
            val values = instanceValueReadService.readAllForInstance(1)

            values shouldHaveSize 2
            values shouldHaveKey "userID"
            values shouldHaveKey "amount"
            values["userID"] shouldBe ("Leonard" to SupportedDataType.STRING)
            values["amount"] shouldBe ("7" to SupportedDataType.LONG)
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent instance") {
            val nonExistentId = 666L
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                instanceValueReadService.readAllForInstance(nonExistentId)
            }

            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }
    }

    context("read") {
        should("read the value by key belonging to the given instance id & return it") {
            val value = instanceValueReadService.readForInstanceByKey(1, "amount")

            value shouldBe ("7" to SupportedDataType.LONG)
        }

        should("return null when the key does not exists for the given instance id") {
            val value = instanceValueReadService.readForInstanceByKey(1, "nonExistentKey")

            value shouldBe null
        }

        should("throw WorkflowInstanceNotFoundException when given non-existent instance") {
            val nonExistentId = 666L
            val exception = shouldThrow<WorkflowInstanceNotFoundException> {
                instanceValueReadService.readForInstanceByKey(nonExistentId, "someKey")
            }

            exception.message shouldBe "Workflow instance with id $nonExistentId could not be found"
        }
    }
})
