package de.partspicker.web.workflow.persistance.entities

import de.partspicker.web.workflow.persistance.entities.enums.InstanceValueTypeEntity
import de.partspicker.web.workflow.persistance.entities.enums.SupportedDataTypeEntity
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "workflow_instance_values")
data class InstanceValueEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instance_value_id_gen")
    @SequenceGenerator(name = "instance_value_id_gen", sequenceName = "instance_value_id_seq", allocationSize = 1)
    val id: Long,

    @ManyToOne
    @JoinColumn(
        name = "workflow_instance_id",
        foreignKey = ForeignKey(name = "fk_instance_values_workflow_instance_id")
    )
    val workflowInstance: InstanceEntity,

    val key: String,

    val value: String,

    @Enumerated(EnumType.STRING)
    val valueDataType: SupportedDataTypeEntity,

    @Enumerated(EnumType.STRING)
    val type: InstanceValueTypeEntity
)
