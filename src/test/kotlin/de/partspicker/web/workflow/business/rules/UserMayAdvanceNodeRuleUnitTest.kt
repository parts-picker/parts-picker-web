package de.partspicker.web.workflow.business.rules

import de.partspicker.web.test.generators.workflow.NodeGenerators
import de.partspicker.web.workflow.business.exceptions.NodeNotAdvanceableByUserRuleException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single

class UserMayAdvanceNodeRuleUnitTest : ShouldSpec({
    should("just run when current node is user action node") {
        // given
        val advanceableNode = NodeGenerators.userActionNodeGenerator.single()

        // when & then
        UserMayAdvanceNodeRule(advanceableNode).valid()
    }

    should("just run when current node is start node") {
        // given
        val advanceableNode = NodeGenerators.startNodeGenerator.single()

        // when & then
        UserMayAdvanceNodeRule(advanceableNode).valid()
    }

    should("throw NodeNotAdvanceableByUserRuleException when node is not advanceable by user") {
        // given
        val node = NodeGenerators.stopNodeGenerator.single()

        // when
        val exception = shouldThrow<NodeNotAdvanceableByUserRuleException> {
            UserMayAdvanceNodeRule(node).valid()
        }

        // then
        exception.message shouldBe "Node of type ${node::class.java.simpleName} cannot be advanced by a user"
    }
})
