package de.partspicker.web.workflow.persistence

import de.partspicker.web.workflow.persistence.entities.nodes.NodeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NodeRepository : JpaRepository<NodeEntity, Long> {
    fun findAllByWorkflowId(workflowId: Long): List<NodeEntity>
    fun findByWorkflowIdAndName(workflowId: Long, name: String): NodeEntity?
}
