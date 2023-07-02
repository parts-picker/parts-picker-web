package de.partspicker.web.common.hal

import org.springframework.hateoas.Link

enum class DefaultName {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    SEARCH
}

fun Link.withName(name: DefaultName) = this.withName(name.name)
