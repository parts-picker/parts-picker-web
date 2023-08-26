package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.objects.InstanceValue
import de.partspicker.web.workflow.persistence.InstanceRepository
import de.partspicker.web.workflow.persistence.InstanceValueRepository
import org.springframework.stereotype.Service

@Service
class InstanceValueReadService(
    private val instanceValueRepository: InstanceValueRepository,
    private val instanceRepository: InstanceRepository
) {
    fun readAllForInstance(instanceId: Long): List<InstanceValue> {
        if (!this.instanceRepository.existsById(instanceId)) {
            throw WorkflowInstanceNotFoundException(instanceId)
        }

        val instanceValueEntities = this.instanceValueRepository.findAllByWorkflowInstanceId(
            workflowInstanceId = instanceId,
        )

        return InstanceValue.AsList.from(instanceValueEntities)
    }

    fun readForInstanceByKey(instanceId: Long, key: String): InstanceValue? {
        if (!this.instanceRepository.existsById(instanceId)) {
            throw WorkflowInstanceNotFoundException(instanceId)
        }

        val instanceValueEntity = this.instanceValueRepository.findByWorkflowInstanceIdAndKey(
            workflowInstanceId = instanceId,
            key = key
        ) ?: return null

        return InstanceValue.from(instanceValueEntity)
    }
}
