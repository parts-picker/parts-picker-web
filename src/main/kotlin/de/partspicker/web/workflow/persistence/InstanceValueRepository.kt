package de.partspicker.web.workflow.persistence

import de.partspicker.web.workflow.persistence.entities.InstanceValueEntity
import de.partspicker.web.workflow.persistence.entities.enums.InstanceValueTypeEntity
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
