package de.partspicker.web.workflow.persistance

import de.partspicker.web.workflow.persistance.entities.EdgeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EdgeRepository : JpaRepository<EdgeEntity, Long> {
    fun findAllByWorkflowId(workflowId: Long): List<EdgeEntity>

    fun findAllBySourceId(sourceId: Long): List<EdgeEntity>

    /**
     * Fetches a single edge which has the node with the given id as source node.
     * Should only be used for nodes that are guaranteed to have only one edge.
     */
    fun readBySourceId(sourceId: Long): EdgeEntity
}
