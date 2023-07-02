package de.partspicker.web.test.util

import de.partspicker.web.common.hal.DefaultName
import org.springframework.hateoas.Link
import org.springframework.hateoas.Links

fun Links.getLink(relation: String, name: String): Link? {
    return this.stream()
        .filter { it.hasRel(relation) && it.name == name }
        .findFirst()
        .orElse(null)
}

fun Links.getLink(relation: String, name: DefaultName) = this.getLink(relation, name.toString())
