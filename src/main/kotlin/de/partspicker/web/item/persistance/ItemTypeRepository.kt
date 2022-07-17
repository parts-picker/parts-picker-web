package de.partspicker.web.item.persistance

import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface ItemTypeRepository : PagingAndSortingRepository<ItemTypeEntity, Long>
