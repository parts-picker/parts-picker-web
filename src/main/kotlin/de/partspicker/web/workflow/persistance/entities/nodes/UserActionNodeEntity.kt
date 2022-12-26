package de.partspicker.web.workflow.persistance.entities.nodes

import de.partspicker.web.workflow.persistance.entities.WorkflowEntity
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("user_action")
class UserActionNodeEntity(
    id: Long,
    name: String,
    workflow: WorkflowEntity,

    @Column
    val displayName: String
) : NodeEntity(
    id = id,
    name = name,
    workflow = workflow
)
