package de.partspicker.web.item.persistance

import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface ItemTypeRepository :
    CrudRepository<ItemTypeEntity, Long>,
    PagingAndSortingRepository<ItemTypeEntity, Long> {
    fun findAllByOrgUnitId(orgUnitId: Long, pageable: Pageable): Page<ItemTypeEntity>
}
