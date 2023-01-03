package de.partspicker.web.workflow.business.objects.enums

import de.partspicker.web.workflow.persistance.entities.enums.StartTypeEntity

enum class StartType {
    USER,
    WORKFLOW,
    SCHEDULE;

    companion object {
        fun from(startTypeEntity: StartTypeEntity) = when (startTypeEntity) {
            StartTypeEntity.USER -> USER
            StartTypeEntity.WORKFLOW -> WORKFLOW
            StartTypeEntity.SCHEDULE -> SCHEDULE
        }
    }
}
