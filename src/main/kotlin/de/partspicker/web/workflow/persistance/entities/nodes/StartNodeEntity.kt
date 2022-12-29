package de.partspicker.web.workflow.persistance.entities.nodes

import de.partspicker.web.workflow.persistance.entities.WorkflowEntity
import de.partspicker.web.workflow.persistance.entities.enums.StartTypeEntity
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

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
