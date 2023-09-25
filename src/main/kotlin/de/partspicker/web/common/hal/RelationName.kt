package de.partspicker.web.common.hal

import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder

enum class RelationName(val displayName: String) {
    ADVANCE("advance"),
    ASSIGNABLE("assignable"),
    ASSIGNABLE_TO("assignableTo"),
    ASSIGNED("assigned"),
    ASSIGNED_TO("assignedTo"),
    AVAILABLE("available"),
    STATUS("status"), // RFC8631
    SUBSET_OF("subsetOf"),
    COPIES("copies"),
    COPIED_FROM("copiedFrom")
}

fun WebMvcLinkBuilder.withRel(rel: RelationName) = this.withRel(rel.displayName)
fun Link.withRel(rel: RelationName) = this.withRel(rel.displayName)
