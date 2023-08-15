package de.partspicker.web.workflow.persistence.entities.migration

import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity
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
@Table(name = "workflow_migration_node_migrations")
data class NodeMigrationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "node_migration_id_gen")
    @SequenceGenerator(name = "node_migration_id_gen", sequenceName = "node_migration_id_seq", allocationSize = 1)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_node_id", foreignKey = ForeignKey(name = "fk_node_migration_source_node"))
    val source: NodeEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_node_id", foreignKey = ForeignKey(name = "fk_node_migration_target_node"))
    val target: NodeEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "migration_plan_id", foreignKey = ForeignKey(name = "fk_node_migration_migration_plan_id"))
    val migrationPlan: MigrationPlanEntity

) {
    companion object {
        fun from(
            sourceNodeEntity: NodeEntity,
            targetNodeEntity: NodeEntity,
            migrationPlanEntity: MigrationPlanEntity
        ) = NodeMigrationEntity(
            id = 0L,
            source = sourceNodeEntity,
            target = targetNodeEntity,
            migrationPlan = migrationPlanEntity
        )
    }
}
