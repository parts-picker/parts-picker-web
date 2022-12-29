package de.partspicker.web.workflow.business.objects.create.enums

import de.partspicker.web.workflow.api.json.enums.StartTypeJson

enum class StartTypeCreate {
    USER,
    WORKFLOW,
    SCHEDULE;

    companion object {
        fun from(startTypeJson: StartTypeJson) = when (startTypeJson) {
            StartTypeJson.USER -> USER
            StartTypeJson.WORKFLOW -> WORKFLOW
            StartTypeJson.SCHEDULE -> SCHEDULE
        }
    }
}
