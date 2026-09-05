package de.partspicker.web.common.util

infix fun Boolean.elseThrow(throwable: Throwable) {
    if (!this) {
        throw throwable
    }
}
