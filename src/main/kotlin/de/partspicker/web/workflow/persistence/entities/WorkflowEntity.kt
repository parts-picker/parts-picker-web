package de.partspicker.web.workflow.persistence.entities

import de.partspicker.web.workflow.business.objects.create.WorkflowCreate
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "workflows")
data class WorkflowEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workflow_id_gen")
    @SequenceGenerator(name = "workflow_id_gen", sequenceName = "workflow_id_seq", allocationSize = 1)
    val id: Long,

    val name: String,

    val version: Long
) {
    @Column(name = "created_on")
    var createdOn: OffsetDateTime? = null
        protected set

    @PrePersist
    @Suppress("UnusedPrivateMember")
    private fun setCreatedOnOnPersist() {
        createdOn = OffsetDateTime.now()
    }

    companion object {
        fun from(workflowCreate: WorkflowCreate) = WorkflowEntity(
            id = 0,
            name = workflowCreate.name,
            version = workflowCreate.version
        )
    }
}
