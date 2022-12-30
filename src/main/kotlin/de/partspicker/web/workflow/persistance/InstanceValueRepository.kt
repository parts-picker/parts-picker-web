package de.partspicker.web.workflow.persistance

import de.partspicker.web.workflow.persistance.entities.InstanceValueEntity
import de.partspicker.web.workflow.persistance.entities.enums.InstanceValueTypeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InstanceValueRepository : JpaRepository<InstanceValueEntity, Long> {
    fun findAllByWorkflowInstanceIdAndType(
        workflowInstanceId: Long,
        type: InstanceValueTypeEntity
    ): List<InstanceValueEntity>

    fun findByWorkflowInstanceIdAndTypeAndKey(
        workflowInstanceId: Long,
        type: InstanceValueTypeEntity,
        key: String
    ): InstanceValueEntity?
}
