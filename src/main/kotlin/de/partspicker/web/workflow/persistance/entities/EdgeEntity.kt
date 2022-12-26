package de.partspicker.web.workflow.persistance.entities

import de.partspicker.web.workflow.persistance.entities.nodes.NodeEntity
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
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
@Table(name = "workflow_edges")
data class EdgeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "edge_id_gen")
    @SequenceGenerator(name = "edge_id_gen", sequenceName = "edge_id_seq", allocationSize = 1)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", foreignKey = ForeignKey(name = "fk_edge_workflow_id"))
    val workflow: WorkflowEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_node_id", foreignKey = ForeignKey(name = "fk_edge_source_node"))
    val source: NodeEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_node_id", foreignKey = ForeignKey(name = "fk_edge_target_node"))
    val target: NodeEntity,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "workflow_condition_keys", joinColumns = [JoinColumn(name = "edge_id")])
    @Column(name = "condition_key")
    val conditionKeys: List<String>
)
