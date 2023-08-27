package de.partspicker.web.workflow.persistence.entities.nodes

import de.partspicker.web.workflow.persistence.entities.WorkflowEntity
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("user_action")
class UserActionNodeEntity(
    id: Long,
    workflow: WorkflowEntity,
    name: String,
    displayName: String,
) : NodeEntity(
    id = id,
    workflow = workflow,
    name = name,
    displayName = displayName
)
