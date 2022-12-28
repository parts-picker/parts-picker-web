package de.partspicker.web.workflow.persistance.entities

import de.partspicker.web.workflow.business.objects.create.EdgeCreate
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
class EdgeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "edge_id_gen")
    @SequenceGenerator(name = "edge_id_gen", sequenceName = "edge_id_seq", allocationSize = 1)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", foreignKey = ForeignKey(name = "fk_edge_workflow_id"))
    val workflow: WorkflowEntity,

    val name: String,

    val displayName: String,

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
) {

    companion object {
        fun from(edgeCreate: EdgeCreate, workflowId: Long, nodeLookUpMap: Map<String, NodeEntity>): EdgeEntity {
            val source = nodeLookUpMap.getValue(edgeCreate.sourceNode)
            val target = nodeLookUpMap.getValue(edgeCreate.targetNode)

            return EdgeEntity(
                id = 0,
                workflow = WorkflowEntity(id = workflowId),
                name = edgeCreate.name,
                displayName = edgeCreate.displayName,
                source = source,
                target = target,
                conditionKeys = edgeCreate.conditions
            )
        }
    }

    object AsList {
        fun from(edgeCreates: Iterable<EdgeCreate>, workflowId: Long, nodeLookUpMap: Map<String, NodeEntity>) =
            edgeCreates.map { from(it, workflowId, nodeLookUpMap) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EdgeEntity) return false

        if (id != other.id) return false
        if (workflow != other.workflow) return false
        if (name != other.name) return false
        if (displayName != other.displayName) return false
        if (source != other.source) return false
        if (target != other.target) return false
        if (conditionKeys != other.conditionKeys) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + workflow.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + source.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + conditionKeys.hashCode()
        return result
    }

    override fun toString(): String {
        return "EdgeEntity(id=$id, name='$name', displayName='$displayName')"
    }
}
