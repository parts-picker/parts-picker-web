package de.partspicker.web.common.hal

import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder

enum class RelationName(val displayName: String) {
    ASSIGNED("assigned"),
    ASSIGNED_TO("assignedTo"),
    ADVANCE("advance"),
    STATUS("status") // RFC8631
}

fun WebMvcLinkBuilder.withRel(rel: RelationName) = this.withRel(rel.displayName)
fun Link.withRel(rel: RelationName) = this.withRel(rel.displayName)
