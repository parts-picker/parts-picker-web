package de.partspicker.web.project.business.objects

import de.partspicker.web.project.persistance.entities.GroupEntity

data class Group(
    val id: Long = 0,
    val name: String? = null,
    val description: String? = null
) {
    companion object {
        fun from(groupEntity: GroupEntity) = Group(
            id = groupEntity.id,
            name = groupEntity.name,
            description = groupEntity.description
        )
    }
}
