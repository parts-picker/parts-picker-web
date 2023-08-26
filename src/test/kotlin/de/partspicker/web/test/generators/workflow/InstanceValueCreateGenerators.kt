package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.business.objects.InstanceValue
import de.partspicker.web.workflow.business.objects.enums.InstanceValueType
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string

class InstanceValueCreateGenerators private constructor() {
    companion object {
        val randomSupportedDataType = Arb.enum<SupportedDataType>()

        val randomInstanceValueType = Arb.enum<InstanceValueType>()

        val generator: Arb<InstanceValue> = Arb.bind(
            Arb.string(range = IntRange(3, 16)),
            randomSupportedDataType,
            randomInstanceValueType

        ) { key, dataType, valueType ->
            val value: String = when (dataType) {
                SupportedDataType.STRING -> Arb.string(range = IntRange(3, 16)).single()
                SupportedDataType.LONG -> Arb.long().single().toString()
                SupportedDataType.INTEGER -> Arb.int().single().toString()
            }

            InstanceValue(
                key = key,
                value = value,
                dataType = dataType,
                valueType = valueType
            )
        }
    }
}
