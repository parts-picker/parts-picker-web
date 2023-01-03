package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.DatatypeNotSupportedException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistance.InstanceRepository
import de.partspicker.web.workflow.persistance.InstanceValueRepository
import de.partspicker.web.workflow.persistance.entities.InstanceEntity
import de.partspicker.web.workflow.persistance.entities.InstanceValueEntity
import de.partspicker.web.workflow.persistance.entities.enums.InstanceValueTypeEntity
import de.partspicker.web.workflow.persistance.entities.enums.SupportedDataTypeEntity
import de.partspicker.web.workflow.persistance.entities.enums.SupportedDataTypeEntity.LONG
import de.partspicker.web.workflow.persistance.entities.enums.SupportedDataTypeEntity.STRING
import org.springframework.stereotype.Service

@Service
class InstanceValueService(
    private val instanceValueRepository: InstanceValueRepository,
    private val instanceRepository: InstanceRepository
) {

    fun setMultipleForInstance(instanceId: Long, values: Map<String, Any>) {
        if (!this.instanceRepository.existsById(instanceId)) {
            throw WorkflowInstanceNotFoundException(instanceId)
        }

        val preparedValues = values.map {
            convert(instanceId, it.key, it.value)
        }

        this.instanceValueRepository.saveAll(preparedValues)
    }

    fun setForInstance(
        instanceId: Long,
        key: String,
        value: Any
    ): Pair<String, SupportedDataType> {
        if (!this.instanceRepository.existsById(instanceId)) {
            throw WorkflowInstanceNotFoundException(instanceId)
        }

        val preparedValue = convert(instanceId, key, value)

        val savedValue = this.instanceValueRepository.save(preparedValue)

        return savedValue.value to SupportedDataType.from(savedValue.valueDataType)
    }

    private fun convert(instanceId: Long, key: String, value: Any): InstanceValueEntity {
        val convertedPair: Pair<String, SupportedDataTypeEntity> = when (value) {
            is Long -> value.toString() to LONG
            is String -> value to STRING
            else -> throw DatatypeNotSupportedException(value.javaClass.simpleName)
        }

        return InstanceValueEntity(
            id = 0,
            workflowInstance = InstanceEntity(id = instanceId),
            key = key,
            value = convertedPair.first,
            valueDataType = convertedPair.second,
            type = InstanceValueTypeEntity.WORKFLOW
        )
    }
}
