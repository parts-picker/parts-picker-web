package de.partspicker.web.test.generators.workflow

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.checkAll

class NodeNameGeneratorsUnitTest : ShouldSpec({

    context("NodeNameGenerator") {
        should("create node names matching the given regex") {
            checkAll(NodeNameGenerators.nodeNameGenerator) {
                it shouldNotBe null
                it shouldMatch "^[a-zA-Z]+\\-\\d+\$"
            }
        }
    }
})
