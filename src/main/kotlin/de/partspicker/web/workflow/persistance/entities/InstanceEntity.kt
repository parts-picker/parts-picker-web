package de.partspicker.web.workflow.persistance.entities

import de.partspicker.web.workflow.persistance.entities.nodes.NodeEntity
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "workflow_instances")
data class InstanceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instance_id_gen")
    @SequenceGenerator(name = "instance_id_gen", sequenceName = "instance_id_seq", allocationSize = 1)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", foreignKey = ForeignKey(name = "fk_instance_workflow_id"))
    val workflow: WorkflowEntity? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_node_id", foreignKey = ForeignKey(name = "fk_current_node"))
    val currentNode: NodeEntity? = null,

    val active: Boolean = false
)
