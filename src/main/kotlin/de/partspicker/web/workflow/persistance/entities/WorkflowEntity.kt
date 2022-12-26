package de.partspicker.web.workflow.persistance.entities

import de.partspicker.web.workflow.persistance.entities.nodes.NodeEntity
import java.time.OffsetDateTime
import javax.persistence.CascadeType.ALL
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "workflows")
data class WorkflowEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workflow_id_gen")
    @SequenceGenerator(name = "workflow_id_gen", sequenceName = "workflow_id_seq", allocationSize = 1)
    val id: Long,

    val name: String,

    val version: Long,

    @OneToMany(mappedBy = "workflow", cascade = [ALL])
    val nodes: Set<NodeEntity>,

    @OneToMany(mappedBy = "workflow", cascade = [ALL])
    val edges: Set<EdgeEntity>,

    val createdOn: OffsetDateTime
)
