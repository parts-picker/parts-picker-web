package de.partspicker.web.workflow.persistence

import de.partspicker.web.workflow.persistence.entities.InstanceEntity
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface InstanceRepository : JpaRepository<InstanceEntity, Long> {

    fun findAllByWorkflowId(id: Long): List<InstanceEntity>

    // query will fail if name of class AutomatedActionNodeEntity was changed
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT i FROM InstanceEntity i WHERE TYPE(i.currentNode) = AutomatedActionNodeEntity")
    fun findAllWhereCurrentNodeIsAutomated(pageable: Pageable): List<InstanceEntity>
}
