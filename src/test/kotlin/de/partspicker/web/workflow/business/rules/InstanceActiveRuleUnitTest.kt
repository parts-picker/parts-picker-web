package de.partspicker.web.workflow.business.rules

import de.partspicker.web.test.generators.workflow.InstanceGenerators
import de.partspicker.web.workflow.business.exceptions.InstanceInactiveException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single

class InstanceActiveRuleUnitTest : ShouldSpec({

    context("valid") {
        should("just run when given active instance") {
            // given
            val instance = InstanceGenerators.generator.single().copy(active = true)

            // when & then
            InstanceActiveRule(instance).valid()
        }

        should("throw InstanceInactiveException when given null") {
            // given
            val instance = null

            // when
            val exception = shouldThrow<IllegalArgumentException> { InstanceActiveRule(instance).valid() }

            // then
            exception.message shouldBe "Cannot be null - this is a bug & will be resolved in the future"
        }

        should("throw InstanceInactiveException when given inactive instance") {
            // given
            val instance = InstanceGenerators.generator.single().copy(active = false)

            // when
            val exception = shouldThrow<InstanceInactiveException> { InstanceActiveRule(instance).valid() }

            // then
            exception.message shouldBe "The instance with the given id ${instance.id} is inactive & cannot be modified"
        }
    }
})
