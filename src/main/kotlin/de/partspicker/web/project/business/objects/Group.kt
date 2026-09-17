package de.partspicker.web.project.business.objects

import de.partspicker.web.project.persistance.entities.GroupEntity

data class Group(
    val id: Long,
    val name: String?,
    val description: String?,
    val orgUnitId: Long,
    val createdById: Long
) {
    companion object {
        fun from(groupEntity: GroupEntity) = Group(
            id = groupEntity.id,
            name = groupEntity.name,
            description = groupEntity.description,
            orgUnitId = groupEntity.orgUnit.id,
            createdById = groupEntity.creation.createdBy.id
        )
    }
}
