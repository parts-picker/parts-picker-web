package de.partspicker.web.common.security

import org.springframework.stereotype.Component

/**
 * Marks the work of the current thread as done by the system rather than by a user.
 * Nested components can use this info to decide whether rule checks are needed.
 */
@Component
class SystemContext {

    private val system = ThreadLocal.withInitial { false }

    fun isSystem(): Boolean = this.system.get()

    /**
     * Runs the given [block] as the system. The previous state is restored afterwards, also when the
     * block throws, so a thread can never be left behind in system mode.
     * Only covers the calling thread; work handed to another thread is checked as usual.
     */
    fun <T> runAsSystem(block: () -> T): T {
        val previous = this.system.get()
        this.system.set(true)

        try {
            return block()
        } finally {
            if (previous) this.system.set(true) else this.system.remove()
        }
    }
}
