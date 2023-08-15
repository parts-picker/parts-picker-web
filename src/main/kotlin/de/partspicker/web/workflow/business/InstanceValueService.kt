package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.DataTypeNotSupportedException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.create.InstanceValueCreate
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistence.InstanceRepository
import de.partspicker.web.workflow.persistence.InstanceValueRepository
import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import de.partspicker.web.workflow.persistence.entities.InstanceValueEntity
import de.partspicker.web.workflow.persistence.entities.enums.InstanceValueTypeEntity
import de.partspicker.web.workflow.persistence.entities.enums.SupportedDataTypeEntity
import de.partspicker.web.workflow.persistence.entities.enums.SupportedDataTypeEntity.INTEGER
import de.partspicker.web.workflow.persistence.entities.enums.SupportedDataTypeEntity.LONG
import de.partspicker.web.workflow.persistence.entities.enums.SupportedDataTypeEntity.STRING
import org.springframework.stereotype.Service

@Service
class InstanceValueService(
    private val instanceValueRepository: InstanceValueRepository,
    private val instanceRepository: InstanceRepository
) {
    companion object {
        const val VALUES_IS_EMPTY = "At least one value must be given to create, but values was empty"
    }

    fun setMultipleForInstance(instanceId: Long, values: List<InstanceValueCreate>) {
        if (!this.instanceRepository.existsById(instanceId)) {
            throw WorkflowInstanceNotFoundException(instanceId)
        }

        val valuesToSave = values.map {
            val existingId = this.instanceValueRepository.findByWorkflowInstanceIdAndTypeAndKey(
                instanceId,
                InstanceValueTypeEntity.WORKFLOW,
                it.key
            )?.id ?: 0L

            InstanceValueEntity.from(existingId, it, instanceId, InstanceValueTypeEntity.WORKFLOW)
        }

        this.instanceValueRepository.saveAll(valuesToSave)
    }

    fun setMultipleWithAutoTypeDetectionForInstance(instanceId: Long, values: Map<String, Any>) {
        if (!this.instanceRepository.existsById(instanceId)) {
            throw WorkflowInstanceNotFoundException(instanceId)
        }

        val valuesToSave = values.map { convert(instanceId, it.key, it.value) }

        this.instanceValueRepository.saveAll(valuesToSave)
    }

    fun setSingleWithAutoTypeDetectionForInstance(
        instanceId: Long,
        key: String,
        value: Any
    ): Pair<String?, SupportedDataType> {
        if (!this.instanceRepository.existsById(instanceId)) {
            throw WorkflowInstanceNotFoundException(instanceId)
        }

        val preparedValue = convert(instanceId, key, value)

        val savedValue = this.instanceValueRepository.save(preparedValue)

        return savedValue.value to SupportedDataType.from(savedValue.valueDataType)
    }

    private fun convert(instanceId: Long, key: String, value: Any): InstanceValueEntity {
        val existingId = this.instanceValueRepository.findByWorkflowInstanceIdAndTypeAndKey(
            instanceId,
            InstanceValueTypeEntity.WORKFLOW,
            key
        )?.id

        val convertedPair: Pair<String?, SupportedDataTypeEntity> = when (value) {
            is Long -> value.toString() to LONG
            is String -> value to STRING
            is Int -> value.toString() to INTEGER
            else -> throw DataTypeNotSupportedException(value.javaClass.simpleName)
        }

        return InstanceValueEntity(
            id = existingId ?: 0,
            workflowInstance = InstanceEntity(id = instanceId),
            key = key,
            value = convertedPair.first,
            valueDataType = convertedPair.second,
            type = InstanceValueTypeEntity.WORKFLOW
        )
    }
}
