package de.partspicker.web.inventory.persistence

import de.partspicker.web.inventory.persistence.AvailableItemTypeSearchRepository.Companion.MAX_HITS
import de.partspicker.web.inventory.persistence.results.AvailableItemTypeResult
import de.partspicker.web.test.util.TestSetupHelper
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import jakarta.transaction.Transactional
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * Updating the indexes must be done manually for this test.
 * Normally, this is handled automatically while committing, but this is not working while using @Transactional.
 */
@SpringBootTest
@ActiveProfiles("integration")
@Transactional
class AvailableItemTypeSearchRepositoryIntTest(
    private val cut: AvailableItemTypeSearchRepository,
    // support classes
    private val testSetupHelper: TestSetupHelper
) : ShouldSpec({

    afterEach {
        // reset index manually after each test
        testSetupHelper.manualSearchClearing()
    }

    // tests
    context("searchByNameFilterRequired") {
        should("return no more than max hits even if more hits exist") {
            // given
            val project = testSetupHelper.setupProject()

            val amountOfTypes = MAX_HITS + 5
            val itemTypeName = "some name"
            testSetupHelper.setupItemTypes(amountToCreate = amountOfTypes, name = itemTypeName)

            testSetupHelper.manualSearchIndexing()

            // when
            val returnedTypes = cut.searchByNameFilterRequired(itemTypeName, project.id)

            // then
            amountOfTypes shouldBeGreaterThan MAX_HITS
            returnedTypes shouldHaveSize MAX_HITS
            returnedTypes.forEach { it.name shouldBe itemTypeName }
        }

        should("return an empty list when no item types are available") {
            // given
            val project = testSetupHelper.setupProject()

            // when
            val returnedTypes = cut.searchByNameFilterRequired("", project.id)

            // then
            returnedTypes.shouldBeEmpty()
        }

        should("return only itemTypes that are not required for the project") {
            // given
            val project = testSetupHelper.setupProject()

            val itemTypeName = "name"
            val type1 = testSetupHelper.setupItemType(itemTypeName)
            val type2 = testSetupHelper.setupItemType(itemTypeName)

            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = type1.id)
            testSetupHelper.manualSearchIndexing()

            // when
            val returnedTypes = cut.searchByNameFilterRequired(itemTypeName, projectId = project.id)

            // then
            returnedTypes shouldHaveSize 1
            returnedTypes shouldContain AvailableItemTypeResult(type2.id, type2.name!!)
        }

        should("return itemTypes whose name is contained in the search string") {
            // given
            val project = testSetupHelper.setupProject()

            val searchString = "name"
            val itemTypeName = "prefix${searchString}suffix"

            val type1 = testSetupHelper.setupItemType(itemTypeName)
            testSetupHelper.setupItemType("somethingElse")
            testSetupHelper.manualSearchIndexing()

            // when
            val returnedTypes = cut.searchByNameFilterRequired(searchString, projectId = project.id)

            // then
            returnedTypes shouldHaveSize 1
            returnedTypes shouldContain AvailableItemTypeResult(type1.id, type1.name!!)
        }

        should("return itemTypes whose name is similar to the search string with a word distance of one") {
            // given
            val project = testSetupHelper.setupProject()
            val itemTypeName = "name"
            val searchString = "nama"

            val type1 = testSetupHelper.setupItemType(itemTypeName)
            testSetupHelper.setupItemType("somethingElse")
            testSetupHelper.manualSearchIndexing()

            // when
            val returnedTypes = cut.searchByNameFilterRequired(searchString, projectId = project.id)

            // then
            returnedTypes shouldHaveSize 1
            returnedTypes shouldContain AvailableItemTypeResult(type1.id, type1.name!!)
        }

        should("return itemTypes whose name is contained in or similar to the search string") {
            // given
            val project = testSetupHelper.setupProject()

            val searchString = "name"
            val similarItemTypeName = "namw"
            val containedItemTypeName = "prefix${searchString}suffix"

            val type1 = testSetupHelper.setupItemType(containedItemTypeName)
            val type2 = testSetupHelper.setupItemType(similarItemTypeName)
            testSetupHelper.manualSearchIndexing()

            // when
            val returnedTypes = cut.searchByNameFilterRequired(searchString, projectId = project.id)

            // then
            returnedTypes shouldHaveSize 2
            returnedTypes shouldContain AvailableItemTypeResult(type1.id, type1.name!!)
            returnedTypes shouldContain AvailableItemTypeResult(type2.id, type2.name!!)
        }
    }
})
