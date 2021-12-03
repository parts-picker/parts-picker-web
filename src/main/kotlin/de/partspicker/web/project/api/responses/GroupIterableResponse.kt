package de.partspicker.web.project.api.responses

import de.partspicker.web.project.persistance.entities.GroupEntity

data class GroupIterableResponse(
    val groups: Iterable<GroupResponse>
)

fun Iterable<GroupEntity>.asResponse() = GroupIterableResponse(groups = map(GroupEntity::asResponse))
