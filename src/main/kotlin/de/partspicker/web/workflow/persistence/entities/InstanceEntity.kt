package de.partspicker.web.workflow.persistence.entities

import de.partspicker.web.workflow.persistence.entities.enums.DisplayTypeEntity
import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity
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
@Table(name = "workflow_instances")
data class InstanceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instance_id_gen")
    @SequenceGenerator(name = "instance_id_gen", sequenceName = "instance_id_seq", allocationSize = 1)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", foreignKey = ForeignKey(name = "fk_instance_workflow_id"))
    var workflow: WorkflowEntity? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_node_id", foreignKey = ForeignKey(name = "fk_current_node"))
    var currentNode: NodeEntity,

    var active: Boolean = false,

    var message: String? = null,

    @Enumerated(EnumType.STRING)
    var displayType: DisplayTypeEntity = DisplayTypeEntity.DEFAULT
)
