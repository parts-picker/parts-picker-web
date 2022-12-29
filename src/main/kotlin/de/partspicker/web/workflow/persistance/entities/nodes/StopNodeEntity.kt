package de.partspicker.web.workflow.persistance.entities.nodes

import de.partspicker.web.workflow.persistance.entities.WorkflowEntity
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

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
