package de.partspicker.web.project.business.rules

import de.partspicker.web.test.generators.ProjectGenerators
import de.partspicker.web.workflow.business.exceptions.InstanceInactiveException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single

class ProjectActiveRuleUnitTest : ShouldSpec({

    context("valid") {
        should("just run when given active instance") {
            // given
            val project = ProjectGenerators.generator.single().copy(active = true)

            // when & then
            ProjectActiveRule(project).valid()
        }

        should("throw InstanceInactiveException when given inactive instance") {
            // given
            val project = ProjectGenerators.generator.single().copy(active = false)

            // when
            val exception = shouldThrow<InstanceInactiveException> {
                ProjectActiveRule(project).valid()
            }

            // then
            exception.message shouldBe "The instance with the given id ${project.workflowInstanceId} is " +
                "inactive & cannot be modified"
        }
    }
})
