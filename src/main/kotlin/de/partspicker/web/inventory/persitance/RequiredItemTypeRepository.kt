package de.partspicker.web.inventory.persitance

import de.partspicker.web.inventory.persitance.embeddableIds.RequiredItemTypeId
import de.partspicker.web.inventory.persitance.entities.RequiredItemTypeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RequiredItemTypeRepository : JpaRepository<RequiredItemTypeEntity, RequiredItemTypeId>
