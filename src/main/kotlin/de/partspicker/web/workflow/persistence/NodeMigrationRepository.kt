package de.partspicker.web.workflow.persistence

import de.partspicker.web.workflow.persistence.entities.migration.NodeMigrationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NodeMigrationRepository : JpaRepository<NodeMigrationEntity, Long> {
    fun findAllByMigrationPlanId(migrationPlanId: Long): List<NodeMigrationEntity>
}
