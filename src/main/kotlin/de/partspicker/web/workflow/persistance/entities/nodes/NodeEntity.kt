package de.partspicker.web.workflow.persistance.entities.nodes

import de.partspicker.web.workflow.business.objects.create.nodes.NodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StartNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.StopNodeCreate
import de.partspicker.web.workflow.business.objects.create.nodes.UserActionNodeCreate
import de.partspicker.web.workflow.persistance.entities.WorkflowEntity
import de.partspicker.web.workflow.persistance.entities.enums.StartTypeEntity
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

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

            is StopNodeCreate -> StopNodeEntity(
                id = 0,
                workflow = WorkflowEntity(id = workflowId),
                name = nodeCreate.name,
                displayName = nodeCreate.displayName
            )
        }
    }

    object AsList {
        fun from(nodeCreates: Iterable<NodeCreate>, workflowId: Long) = nodeCreates.map { from(it, workflowId) }
    }
}
