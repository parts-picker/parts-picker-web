package de.partspicker.web.workflow.persistence.entities.nodes

import de.partspicker.web.workflow.persistence.entities.WorkflowEntity
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("automatic_action")
class AutomatedActionNodeEntity(
    id: Long,
    workflow: WorkflowEntity,
    name: String,

    val displayName: String,
    val automatedActionName: String
) : NodeEntity(
    id = id,
    workflow = workflow,
    name = name
)
