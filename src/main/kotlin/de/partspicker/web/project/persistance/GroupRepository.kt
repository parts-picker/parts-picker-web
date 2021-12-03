package de.partspicker.web.project.persistance

import de.partspicker.web.project.persistance.entities.GroupEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository : JpaRepository<GroupEntity, Long>
