package de.partspicker.web.workflow.persistance.entities

import de.partspicker.web.workflow.business.objects.create.WorkflowCreate
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.PrePersist
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "workflows")
data class WorkflowEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workflow_id_gen")
    @SequenceGenerator(name = "workflow_id_gen", sequenceName = "workflow_id_seq", allocationSize = 1)
    val id: Long,

    @Column(nullable = false)
    val name: String? = null,

    @Column(nullable = false)
    val version: Long? = null
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
