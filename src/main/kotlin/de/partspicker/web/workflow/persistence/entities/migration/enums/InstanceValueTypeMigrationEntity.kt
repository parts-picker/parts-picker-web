package de.partspicker.web.workflow.persistence.entities.migration.enums

import de.partspicker.web.workflow.business.objects.create.migration.enums.InstanceValueTypeCreate

enum class InstanceValueTypeMigrationEntity {
    WORKFLOW,
    SYSTEM;

    companion object {
        fun from(instanceValueTypeCreate: InstanceValueTypeCreate) = when (instanceValueTypeCreate) {
            InstanceValueTypeCreate.WORKFLOW -> WORKFLOW
            InstanceValueTypeCreate.SYSTEM -> SYSTEM
        }
    }
}
