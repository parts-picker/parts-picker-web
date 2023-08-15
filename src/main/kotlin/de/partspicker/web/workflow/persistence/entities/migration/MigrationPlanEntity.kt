package de.partspicker.web.workflow.persistence.entities.migration

import de.partspicker.web.workflow.persistence.entities.WorkflowEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "workflow_migration_plans")
data class MigrationPlanEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "migration_plan_id_gen")
    @SequenceGenerator(name = "migration_plan_id_gen", sequenceName = "migration_plan_id_seq", allocationSize = 1)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_workflow_id", foreignKey = ForeignKey(name = "fk_target_workflow_id"))
    val targetWorkflow: WorkflowEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_workflow_id", foreignKey = ForeignKey(name = "fk_source_workflow_id"))
    val sourceWorkflow: WorkflowEntity,
) {
    companion object {
        fun from(
            sourceWorkflowEntity: WorkflowEntity,
            targetWorkflowEntity: WorkflowEntity
        ) = MigrationPlanEntity(
            id = 0L,
            sourceWorkflow = sourceWorkflowEntity,
            targetWorkflow = targetWorkflowEntity
        )
    }
}
