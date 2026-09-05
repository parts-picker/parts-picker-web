package de.partspicker.web.common.security

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlin.concurrent.thread

class SystemContextUnitTest : ShouldSpec({

    val cut = SystemContext()

    context("isSystem") {
        should("return false when no system block is running") {
            cut.isSystem() shouldBe false
        }

        should("return true while a system block runs") {
            cut.runAsSystem { cut.isSystem() } shouldBe true
        }
    }

    context("runAsSystem") {
        should("return the result of the given block") {
            cut.runAsSystem { "result" } shouldBe "result"
        }

        should("restore the previous state after the given block ran") {
            cut.runAsSystem { }

            cut.isSystem() shouldBe false
        }

        should("restore the previous state when the given block throws") {
            shouldThrow<IllegalStateException> {
                cut.runAsSystem { throw IllegalStateException("boom") }
            }

            cut.isSystem() shouldBe false
        }

        should("keep the thread marked as system until the outer block ends when nested") {
            cut.runAsSystem {
                cut.runAsSystem { }

                cut.isSystem()
            } shouldBe true
        }

        should("not mark any other thread as system") {
            var otherThreadState = true

            cut.runAsSystem {
                val otherThread = thread { otherThreadState = cut.isSystem() }
                otherThread.join()
            }

            otherThreadState shouldBe false
        }
    }
})
