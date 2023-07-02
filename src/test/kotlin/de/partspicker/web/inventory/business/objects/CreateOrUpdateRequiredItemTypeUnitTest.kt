package de.partspicker.web.inventory.business.objects

import de.partspicker.web.test.generators.inventory.CreateOrUpdateRequiredItemTypeGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.single
import java.lang.IllegalArgumentException

class CreateOrUpdateRequiredItemTypeUnitTest : ShouldSpec({

    context("validation") {
        should("run when everything valid") {
            CreateOrUpdateRequiredItemTypeGenerators.generator.single()
        }

        should("throw Exception when requiredAmount is smaller or equal to zero") {
            val exception = shouldThrow<IllegalArgumentException> {
                CreateOrUpdateRequiredItemTypeGenerators.generator.single().copy(requiredAmount = 0)
            }

            exception.message shouldBe
                "The requiredAmount of a CreateOrUpdateRequiredItemType must be greater than zero"
        }
    }
})
