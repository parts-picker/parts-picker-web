package de.partspicker.web.workflow.business.objects.enums

import de.partspicker.web.workflow.persistence.entities.enums.InstanceValueTypeEntity
import de.partspicker.web.workflow.persistence.entities.migration.enums.InstanceValueTypeMigrationEntity

enum class InstanceValueType {
    WORKFLOW,
    SYSTEM;

    companion object {
        fun from(instanceValueTypeEntity: InstanceValueTypeEntity) = when (instanceValueTypeEntity) {
            InstanceValueTypeEntity.WORKFLOW -> WORKFLOW
            InstanceValueTypeEntity.SYSTEM -> SYSTEM
        }

        fun from(
            instanceValueTypeMigrationEntity: InstanceValueTypeMigrationEntity
        ) = when (instanceValueTypeMigrationEntity) {
            InstanceValueTypeMigrationEntity.WORKFLOW -> WORKFLOW
            InstanceValueTypeMigrationEntity.SYSTEM -> SYSTEM
        }
    }
}
