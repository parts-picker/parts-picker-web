package de.parts_picker.web.project.api.responses

import de.parts_picker.web.project.persistance.entities.GroupEntity

data class GroupIterableResponse (
    val groups: Iterable<GroupResponse>
)

fun Iterable<GroupEntity>.asResponse() = GroupIterableResponse(groups = map(GroupEntity::asResponse))