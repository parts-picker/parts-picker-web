package de.partspicker.web.workflow.persistence

import de.partspicker.web.workflow.persistence.entities.migration.MigrationPlanEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MigrationPlanRepository : JpaRepository<MigrationPlanEntity, Long> {
    fun findBySourceWorkflowIdAndTargetWorkflowId(
        sourceWorkflowId: Long,
        targetWorkflowId: Long
    ): MigrationPlanEntity?
}
