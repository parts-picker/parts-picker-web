package de.parts_picker.web.item.persistance

import de.parts_picker.web.item.persistance.entities.ItemEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository: JpaRepository<ItemEntity, Long>