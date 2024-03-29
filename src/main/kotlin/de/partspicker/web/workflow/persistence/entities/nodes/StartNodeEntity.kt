package de.partspicker.web.workflow.persistence.entities.nodes

import de.partspicker.web.workflow.persistence.entities.WorkflowEntity
import de.partspicker.web.workflow.persistence.entities.enums.StartTypeEntity
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
@DiscriminatorValue("start")
class StartNodeEntity(
    id: Long,
    workflow: WorkflowEntity,
    name: String,
    displayName: String,

    @Column
    @Enumerated(EnumType.STRING)
    val startType: StartTypeEntity
) : NodeEntity(
    id = id,
    workflow = workflow,
    name = name,
    displayName = displayName
)
