package de.partspicker.web.workflow.persistence

import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InstanceRepository : JpaRepository<InstanceEntity, Long> {
    fun findAllByWorkflowId(id: Long): List<InstanceEntity>
}
