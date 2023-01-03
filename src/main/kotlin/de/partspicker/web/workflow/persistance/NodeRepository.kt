package de.partspicker.web.workflow.persistance

import de.partspicker.web.workflow.persistance.entities.nodes.NodeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NodeRepository : JpaRepository<NodeEntity, Long> {
    fun findAllByWorkflowId(workflowId: Long): List<NodeEntity>
    fun findByWorkflowIdAndName(workflowId: Long, name: String): NodeEntity?
}
