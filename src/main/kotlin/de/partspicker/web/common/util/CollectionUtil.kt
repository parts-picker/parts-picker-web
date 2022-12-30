package de.partspicker.web.common.util // ktlint-disable filename

infix fun <T : Any> Iterable<T>.intersects(other: Iterable<T>) = any(other::contains)

fun <T : Any> Iterable<T>.duplicates() = this.filter { item -> this.count { it == item } > 1 }.toSet()
