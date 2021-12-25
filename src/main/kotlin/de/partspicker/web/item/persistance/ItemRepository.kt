package de.partspicker.web.item.persistance

import de.partspicker.web.item.persistance.entities.ItemEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository : JpaRepository<ItemEntity, Long> {
    fun findAllByTypeId(itemTypeId: Long): List<ItemEntity>
    fun deleteAllByTypeId(itemTypeId: Long): Long
}
