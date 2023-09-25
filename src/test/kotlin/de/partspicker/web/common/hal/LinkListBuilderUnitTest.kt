package de.partspicker.web.common.hal

import de.partspicker.web.test.rules.TestRule.Companion.INVALID_RULE
import de.partspicker.web.test.rules.TestRule.Companion.VALID_RULE
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainOnly
import org.springframework.hateoas.Link

class LinkListBuilderUnitTest : ShouldSpec({

    should("return all given links") {
        // given
        val link1 = Link.of("bla")
        val link2 = Link.of("aha")

        val builder = LinkListBuilder()
        builder.with(link1)
        builder.with(link2)

        // when
        val links = builder.build()

        // then
        links shouldContainOnly listOf(link1, link2)
    }

    should("return all given links with valid rules") {
        // given
        val link1 = Link.of("bla")
        val link2 = Link.of("aha")

        val builder = LinkListBuilder()
        builder.with(link1, VALID_RULE)
        builder.with(link2, VALID_RULE)

        // when
        val links = builder.build()

        // then
        links shouldContainOnly listOf(link1, link2)
    }

    should("return all given links with valid rules and none with invalid ones") {
        // given
        val link1 = Link.of("bla")
        val link2 = Link.of("aha")

        val builder = LinkListBuilder()
        builder.with(link1, VALID_RULE)
        builder.with(link2, INVALID_RULE)

        // when
        val links = builder.build()

        // then
        links shouldContainOnly listOf(link1)
    }

    should("return all given links which fulfill the condition and none that do not") {
        // given
        val link1 = Link.of("bla")
        val link2 = Link.of("aha")

        val builder = LinkListBuilder()
        builder.withCondition(link1, true)
        builder.withCondition(link2, false)

        // when
        val links = builder.build()

        // then
        links shouldContainOnly listOf(link1)
    }
})
