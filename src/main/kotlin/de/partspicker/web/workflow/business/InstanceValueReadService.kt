package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.enums.SupportedDataType
import de.partspicker.web.workflow.persistance.InstanceRepository
import de.partspicker.web.workflow.persistance.InstanceValueRepository
import de.partspicker.web.workflow.persistance.entities.enums.InstanceValueTypeEntity.WORKFLOW
import org.springframework.stereotype.Service

@Service
class InstanceValueReadService(
    private val instanceValueRepository: InstanceValueRepository,
    private val instanceRepository: InstanceRepository
) {
    fun readAllForInstance(instanceId: Long): Map<String, Pair<String, SupportedDataType>> {
        if (!this.instanceRepository.existsById(instanceId)) {
            throw WorkflowInstanceNotFoundException(instanceId)
        }

        val instanceValues = this.instanceValueRepository.findAllByWorkflowInstanceIdAndType(
            workflowInstanceId = instanceId,
            type = WORKFLOW
        )

        return instanceValues.associateBy({ it.key }, { it.value to SupportedDataType.from(it.valueDataType) })
    }

    fun readForInstanceByKey(instanceId: Long, key: String): Pair<String, SupportedDataType>? {
        if (!this.instanceRepository.existsById(instanceId)) {
            throw WorkflowInstanceNotFoundException(instanceId)
        }

        val instanceValueEntity = this.instanceValueRepository.findByWorkflowInstanceIdAndTypeAndKey(
            workflowInstanceId = instanceId,
            type = WORKFLOW,
            key = key

        ) ?: return null

        return instanceValueEntity.value to SupportedDataType.from(instanceValueEntity.valueDataType)
    }
}
