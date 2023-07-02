package de.partspicker.web.item.persistance

import de.partspicker.web.item.persistance.entities.ItemEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ItemRepository : JpaRepository<ItemEntity, Long> {
    fun findAllByTypeId(itemTypeId: Long, pageable: Pageable): Page<ItemEntity>
    fun deleteAllByTypeId(itemTypeId: Long): Long

    @Query(
        value = "SELECT i FROM ItemEntity i WHERE i.type.id = ?1 " +
            "AND i.assignedProject.id IS NULL " +
            "AND i.status = 'IN_STOCK' " +
            "AND (i.condition = 'NEW' OR i.condition = 'USED' OR i.condition = 'WRAPPED')",
    )
    fun findAllAssignableByTypeId(itemTypeId: Long, pageable: Pageable): Page<ItemEntity>

    fun findAllByAssignedProjectIdAndTypeId(projectId: Long, itemTypeId: Long, pageable: Pageable): Page<ItemEntity>

    fun countByAssignedProjectIdAndTypeId(projectId: Long, itemTypeId: Long): Long
}
