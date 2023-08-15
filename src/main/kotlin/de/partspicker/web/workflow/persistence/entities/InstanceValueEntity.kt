package de.partspicker.web.workflow.persistence.entities

import de.partspicker.web.workflow.business.objects.create.InstanceValueCreate
import de.partspicker.web.workflow.persistence.entities.enums.InstanceValueTypeEntity
import de.partspicker.web.workflow.persistence.entities.enums.SupportedDataTypeEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

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

    val value: String?,

    @Enumerated(EnumType.STRING)
    val valueDataType: SupportedDataTypeEntity,

    @Enumerated(EnumType.STRING)
    val type: InstanceValueTypeEntity
) {
    companion object {
        fun from(
            id: Long,
            instanceValueCreate: InstanceValueCreate,
            instanceId: Long,
            instanceValueEntity: InstanceValueTypeEntity
        ) = InstanceValueEntity(
            id = id,
            workflowInstance = InstanceEntity(id = instanceId),
            key = instanceValueCreate.key,
            value = instanceValueCreate.value,
            valueDataType = SupportedDataTypeEntity.from(instanceValueCreate.type),
            type = instanceValueEntity
        )
    }
}
