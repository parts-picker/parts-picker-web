package de.partspicker.web.workflow.persistence.entities.nodes

import de.partspicker.web.workflow.persistence.entities.WorkflowEntity
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("stop")
class StopNodeEntity(
    id: Long,
    workflow: WorkflowEntity,
    name: String,

    @Column
    val displayName: String
) : NodeEntity(
    id = id,
    workflow = workflow,
    name = name
)
