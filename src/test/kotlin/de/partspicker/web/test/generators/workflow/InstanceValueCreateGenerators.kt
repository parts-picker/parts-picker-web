package de.partspicker.web.test.generators.workflow

import de.partspicker.web.workflow.business.objects.create.InstanceValueCreate
import de.partspicker.web.workflow.business.objects.create.enums.SupportedDataTypeCreate
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string

class InstanceValueCreateGenerators private constructor() {
    companion object {
        val randomSupportedDataTypeCreate = Arb.enum<SupportedDataTypeCreate>()

        val generator: Arb<InstanceValueCreate> = Arb.bind(
            Arb.string(range = IntRange(3, 16)),
            randomSupportedDataTypeCreate

        ) { key, type ->
            val value: String = when (type) {
                SupportedDataTypeCreate.STRING -> Arb.string(range = IntRange(3, 16)).single()
                SupportedDataTypeCreate.LONG -> Arb.long().single().toString()
                SupportedDataTypeCreate.INTEGER -> Arb.int().single().toString()
            }

            InstanceValueCreate(
                key = key,
                value = value,
                type = type
            )
        }
    }
}
