package de.partspicker.web.workflow.persistance.entities.enums

import de.partspicker.web.workflow.business.objects.create.enums.StartTypeCreate

// Limited to 16 chars in db
enum class StartTypeEntity {
    USER,
    WORKFLOW,
    SCHEDULE;

    companion object {
        fun from(startTypeCreate: StartTypeCreate) = when (startTypeCreate) {
            StartTypeCreate.USER -> USER
            StartTypeCreate.WORKFLOW -> WORKFLOW
            StartTypeCreate.SCHEDULE -> SCHEDULE
        }
    }
}
