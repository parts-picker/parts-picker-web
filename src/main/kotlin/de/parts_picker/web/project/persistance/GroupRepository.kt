package de.parts_picker.web.project.persistance

import de.parts_picker.web.project.persistance.entities.GroupEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository: JpaRepository<GroupEntity, Long>