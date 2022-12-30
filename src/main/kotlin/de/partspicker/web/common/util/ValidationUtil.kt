package de.partspicker.web.common.util // ktlint-disable filename

infix fun Boolean.elseThrow(throwable: Throwable) {
    if (!this) {
        throw throwable
    }
}
