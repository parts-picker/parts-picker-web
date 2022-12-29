package de.partspicker.web.workflow.persistance.entities.nodes

import de.partspicker.web.workflow.business.objects.create.nodes.NodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StartNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.UserActionNodeCreate
import de.partspicker.web.workflow.persistance.entities.WorkflowEntity
import de.partspicker.web.workflow.persistance.entities.enums.StartTypeEntity
import javax.persistence.DiscriminatorColumn
import javax.persistence.DiscriminatorType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "workflow_nodes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "node_type", discriminatorType = DiscriminatorType.STRING)
abstract class NodeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "node_id_gen")
    @SequenceGenerator(name = "node_id_gen", sequenceName = "node_id_seq", allocationSize = 1)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", foreignKey = ForeignKey(name = "fk_node_workflow_id"))
    val workflow: WorkflowEntity,

    val name: String
) {
    companion object {
        fun from(nodeCreate: NodeCreate, workflowId: Long): NodeEntity = when (nodeCreate) {
            is UserActionNodeCreate -> UserActionNodeEntity(
                id = 0,
                workflow = WorkflowEntity(id = workflowId),
                name = nodeCreate.name,
                displayName = nodeCreate.displayName
            )

            is StartNodeCreate -> StartNodeEntity(
                id = 0,
                workflow = WorkflowEntity(id = workflowId),
                name = nodeCreate.name,
                displayName = nodeCreate.displayName,
                startType = StartTypeEntity.from(nodeCreate.startType)
            )
        }
    }

    object AsList {
        fun from(nodeCreates: Iterable<NodeCreate>, workflowId: Long) = nodeCreates.map { from(it, workflowId) }
    }
}
