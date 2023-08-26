package de.partspicker.web.workflow.business.objects

import de.partspicker.web.workflow.business.exceptions.UnsupportedDataTypeException
import de.partspicker.web.workflow.business.objects.enums.InstanceValueType
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistence.entities.InstanceValueEntity

data class InstanceValue(
    val key: String,
    val value: String?,
    val dataType: SupportedDataType,
    val valueType: InstanceValueType
) {
    companion object {
        fun fromWithAutoTypeDetection(keyValuePair: Pair<String, Any>): InstanceValue {
            val value = keyValuePair.second
            val dataType = when (value) {
                is Long -> SupportedDataType.LONG
                is String -> SupportedDataType.STRING
                is Int -> SupportedDataType.INTEGER
                else -> throw UnsupportedDataTypeException(value.javaClass.simpleName)
            }

            return InstanceValue(
                key = keyValuePair.first,
                value = value.toString(),
                dataType = dataType,
                valueType = InstanceValueType.WORKFLOW
            )
        }

        fun from(instanceValueEntity: InstanceValueEntity) = InstanceValue(
            key = instanceValueEntity.key,
            value = instanceValueEntity.value,
            dataType = SupportedDataType.from(instanceValueEntity.valueDataType),
            valueType = InstanceValueType.from(instanceValueEntity.type)
        )
    }

    object AsList {
        fun fromWithAutoTypeDetection(values: Map<String, Any>) =
            values.map { fromWithAutoTypeDetection(it.key to it.value) }

        fun from(instanceValueEntities: Iterable<InstanceValueEntity>) = instanceValueEntities.map { from(it) }
    }
}
