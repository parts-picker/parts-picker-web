package de.partspicker.web.workflow.persistance

import de.partspicker.web.workflow.persistance.entities.WorkflowEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface WorkflowRepository : JpaRepository<WorkflowEntity, Long> {
    fun existsByNameAndVersion(name: String, version: Long): Boolean
    fun findByNameAndVersion(name: String, version: Long): Optional<WorkflowEntity>

    @Query(
        value = "SELECT version FROM workflows WHERE name = ?1 AND" +
            " version = (SELECT MAX(version) FROM workflows WHERE name = ?1)",
        nativeQuery = true
    )
    fun findLatestVersion(name: String): Long?
}
