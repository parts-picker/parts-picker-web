package de.partspicker.web.workflow.persistance

import de.partspicker.web.workflow.persistance.entities.EdgeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EdgeRepository : JpaRepository<EdgeEntity, Long> {
    fun findAllByWorkflowId(workflowId: Long): List<EdgeEntity>

    fun findAllBySourceId(sourceId: Long): List<EdgeEntity>
}
