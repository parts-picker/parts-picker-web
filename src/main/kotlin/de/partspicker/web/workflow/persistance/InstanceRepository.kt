package de.partspicker.web.workflow.persistance

import de.partspicker.web.workflow.persistance.entities.InstanceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InstanceRepository : JpaRepository<InstanceEntity, Long>
