package de.partspicker.web.common.hal

import org.springframework.hateoas.Link
import org.springframework.http.HttpMethod

// Link constructor will be protected & not deprecated anymore in spring hateoas 1.4.x
class ExtendedLink(
    base: Link,
    val methods: Set<HttpMethod> = emptySet(),
) : Link(base.href, base.rel)

fun Link.withMethods(vararg methods: HttpMethod) = ExtendedLink(this, methods.toSet())
