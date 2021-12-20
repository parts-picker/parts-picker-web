package de.partspicker.web.item.persistance

import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ItemTypeRepository : JpaRepository<ItemTypeEntity, Long>
