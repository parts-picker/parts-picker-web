package de.partspicker.web.inventory.persistence

import de.partspicker.web.inventory.persistence.embeddableids.RequiredItemTypeId
import de.partspicker.web.inventory.persistence.entities.RequiredItemTypeEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.stream.Stream

interface RequiredItemTypeRepository : JpaRepository<RequiredItemTypeEntity, RequiredItemTypeId> {
    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<RequiredItemTypeEntity>
    fun findByProjectIdAndItemTypeId(projectId: Long, itemTypeId: Long): Optional<RequiredItemTypeEntity>
    fun streamAllByProjectId(projectId: Long): Stream<RequiredItemTypeEntity>

    fun deleteAllByProjectId(projectId: Long)
}
