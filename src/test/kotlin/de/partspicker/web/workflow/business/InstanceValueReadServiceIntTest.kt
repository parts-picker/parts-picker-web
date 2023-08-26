package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.objects.InstanceValue
import de.partspicker.web.workflow.business.objects.enums.InstanceValueType
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
            values.shouldContainOnly(
                listOf(
                    InstanceValue("userID", "Leonard", SupportedDataType.STRING, InstanceValueType.WORKFLOW),
                    InstanceValue("amount", "7", SupportedDataType.LONG, InstanceValueType.SYSTEM)
                )
            )
        }
    }

    context("readForInstanceByKey") {
        should("read the value by key belonging to the instance with the given id & return it") {
            val key = "amount"
            val value = instanceValueReadService.readForInstanceByKey(100, key)

            value shouldNotBe null
            value!!.key shouldBe key
            value.value shouldBe "7"
            value.dataType shouldBe SupportedDataType.LONG
            value.valueType shouldBe InstanceValueType.SYSTEM
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
