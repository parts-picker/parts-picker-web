package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
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
        should("read all instance values belonging to the instance with the given id & return them") {
            val values = instanceValueReadService.readAllForInstance(100)

            values shouldHaveSize 2
            values shouldHaveKey "userID"
            values shouldHaveKey "amount"
            values["userID"] shouldBe ("Leonard" to SupportedDataType.STRING)
            values["amount"] shouldBe ("7" to SupportedDataType.LONG)
        }
    }

    context("readForInstanceByKey") {
        should("read the value by key belonging to the instance with the given id & return it") {
            val value = instanceValueReadService.readForInstanceByKey(100, "amount")

            value shouldBe ("7" to SupportedDataType.LONG)
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
