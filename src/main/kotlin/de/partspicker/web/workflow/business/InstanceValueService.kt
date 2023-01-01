package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistance.InstanceRepository
import de.partspicker.web.workflow.persistance.InstanceValueRepository
import de.partspicker.web.workflow.persistance.entities.InstanceEntity
import de.partspicker.web.workflow.persistance.entities.InstanceValueEntity
import de.partspicker.web.workflow.persistance.entities.enums.InstanceValueTypeEntity
import de.partspicker.web.workflow.persistance.entities.enums.SupportedDataTypeEntity
import org.springframework.stereotype.Service

@Service
class InstanceValueService(
    private val instanceValueRepository: InstanceValueRepository,
    private val instanceRepository: InstanceRepository
) {

    fun setMultipleForInstance(instanceId: Long, values: Map<String, Pair<Any, SupportedDataType>>) {
        if (!this.instanceRepository.existsById(instanceId)) {
            throw WorkflowInstanceNotFoundException(instanceId)
        }

        val preparedValues = values.map {
            InstanceValueEntity(
                id = 0,
                workflowInstance = InstanceEntity(id = instanceId),
                key = it.key,
                value = it.value.first.toString(),
                valueDataType = SupportedDataTypeEntity.from(it.value.second),
                type = InstanceValueTypeEntity.WORKFLOW
            )
        }

        this.instanceValueRepository.saveAll(preparedValues)
    }

    fun setForInstance(
        instanceId: Long,
        key: String,
        value: Any,
        supportedDataType: SupportedDataType
    ): Pair<String, SupportedDataType> {
        if (!this.instanceRepository.existsById(instanceId)) {
            throw WorkflowInstanceNotFoundException(instanceId)
        }

        val preparedValue = InstanceValueEntity(
            id = 0,
            workflowInstance = InstanceEntity(id = instanceId),
            key = key,
            value = value.toString(),
            valueDataType = SupportedDataTypeEntity.from(supportedDataType),
            type = InstanceValueTypeEntity.WORKFLOW
        )

        val savedValue = this.instanceValueRepository.save(preparedValue)

        return savedValue.value to SupportedDataType.from(savedValue.valueDataType)
    }
}
