package de.partspicker.web.project.persistance

import de.partspicker.web.project.persistance.entities.GroupEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository : JpaRepository<GroupEntity, Long> {
    fun findAllByOrgUnitId(orgUnitId: Long, pageable: Pageable): Page<GroupEntity>
}
