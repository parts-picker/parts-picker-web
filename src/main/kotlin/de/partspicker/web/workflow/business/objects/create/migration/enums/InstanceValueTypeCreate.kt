package de.partspicker.web.workflow.business.objects.create.migration.enums

import de.partspicker.web.workflow.api.json.migration.enums.InstanceValueTypeJson

enum class InstanceValueTypeCreate {
    WORKFLOW,
    SYSTEM;

    companion object {
        fun from(instanceValueTypeJson: InstanceValueTypeJson) = when (instanceValueTypeJson) {
            InstanceValueTypeJson.WORKFLOW -> WORKFLOW
            InstanceValueTypeJson.SYSTEM -> SYSTEM
        }
    }
}
