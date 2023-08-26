package de.partspicker.web.workflow.persistence.entities.migration

import de.partspicker.web.workflow.persistence.entities.migration.enums.InstanceValueTypeMigrationEntity
import de.partspicker.web.workflow.persistence.entities.migration.enums.SupportedDataTypeMigrationEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
@Table(name = "workflow_migration_instance_value_migrations")
data class InstanceValueMigrationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instance_value_migration_id_gen")
    @SequenceGenerator(
        name = "instance_value_migration_id_gen",
        sequenceName = "instance_value_migration_id_seq",
        allocationSize = 1
    )
    val id: Long,
    val key: String,
    val value: String?,

    @Enumerated(EnumType.STRING)
    val dataType: SupportedDataTypeMigrationEntity,

    @Enumerated(EnumType.STRING)
    val valueType: InstanceValueTypeMigrationEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_migration_id", foreignKey = ForeignKey(name = "fk_node_migration_id"))
    val nodeMigration: NodeMigrationEntity
)
