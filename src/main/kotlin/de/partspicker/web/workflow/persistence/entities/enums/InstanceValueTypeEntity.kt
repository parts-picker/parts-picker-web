package de.partspicker.web.workflow.persistence.entities.enums

import de.partspicker.web.workflow.business.objects.enums.InstanceValueType

enum class InstanceValueTypeEntity {
    WORKFLOW,
    SYSTEM;

    companion object {
        fun from(instanceValueType: InstanceValueType) = when (instanceValueType) {
            InstanceValueType.WORKFLOW -> WORKFLOW
            InstanceValueType.SYSTEM -> SYSTEM
        }
    }
}
