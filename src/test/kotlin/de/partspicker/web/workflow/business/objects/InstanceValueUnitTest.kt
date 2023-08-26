package de.partspicker.web.workflow.business.objects

import de.partspicker.web.workflow.business.exceptions.UnsupportedDataTypeException
import de.partspicker.web.workflow.business.objects.enums.InstanceValueType
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

class InstanceValueUnitTest : ShouldSpec({

    context("fromWithAutoTypeDetection") {
        should("return InstanceValue when given valid key value pair with a supported data type") {
            // given
            val key = "key"
            val value = 1L

            // when
            val instanceValue = InstanceValue.fromWithAutoTypeDetection(key to value)

            // then
            instanceValue.key shouldBe key
            instanceValue.value shouldBe value.toString()
            instanceValue.dataType shouldBe SupportedDataType.LONG
            instanceValue.valueType shouldBe InstanceValueType.WORKFLOW
        }

        should("throw UnsupportedDataTypeException when given key value pair with unsupported data type") {
            // given
            val key = "key"
            val value: Boolean = true

            // when
            val exception = shouldThrow<UnsupportedDataTypeException> {
                InstanceValue.fromWithAutoTypeDetection(key to value)
            }

            // then
            exception.message shouldStartWith "The given data with datatype 'Boolean' is not supported."
        }
    }
})
