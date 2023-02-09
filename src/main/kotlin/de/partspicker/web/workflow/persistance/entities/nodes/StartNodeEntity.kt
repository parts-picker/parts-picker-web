package de.partspicker.web.workflow.persistance.entities.nodes

import de.partspicker.web.workflow.persistance.entities.WorkflowEntity
import de.partspicker.web.workflow.persistance.entities.enums.StartTypeEntity
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

    @Column
    val displayName: String,

    @Column
    @Enumerated(EnumType.STRING)
    val startType: StartTypeEntity
) : NodeEntity(
    id = id,
    workflow = workflow,
    name = name
)
