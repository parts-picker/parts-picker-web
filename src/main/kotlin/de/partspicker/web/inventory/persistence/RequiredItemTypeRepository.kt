package de.partspicker.web.inventory.persistence

import de.partspicker.web.inventory.persistence.embeddableids.RequiredItemTypeId
import de.partspicker.web.inventory.persistence.entities.RequiredItemTypeEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface RequiredItemTypeRepository : JpaRepository<RequiredItemTypeEntity, RequiredItemTypeId> {
    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<RequiredItemTypeEntity>
}
