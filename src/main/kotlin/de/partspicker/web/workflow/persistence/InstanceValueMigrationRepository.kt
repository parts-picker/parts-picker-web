package de.partspicker.web.workflow.persistence

import de.partspicker.web.workflow.persistence.entities.migration.InstanceValueMigrationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InstanceValueMigrationRepository : JpaRepository<InstanceValueMigrationEntity, Long> {
    fun findAllByNodeMigrationId(nodeMigrationId: Long): List<InstanceValueMigrationEntity>
}
