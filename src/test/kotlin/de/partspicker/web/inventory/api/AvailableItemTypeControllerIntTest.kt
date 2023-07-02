package de.partspicker.web.inventory.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.common.hal.DefaultName
import de.partspicker.web.common.hal.RelationName.ASSIGNED
import de.partspicker.web.common.hal.RelationName.SUBSET_OF
import de.partspicker.web.inventory.api.resources.AvailableItemTypeResource
import de.partspicker.web.inventory.persistence.AvailableItemTypeSearchRepository.Companion.MAX_HITS
import de.partspicker.web.test.util.TestSetupHelper
import de.partspicker.web.test.util.getLink
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldEndWith
import jakarta.transaction.Transactional
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.IanaLinkRelations.COLLECTION
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
class AvailableItemTypeControllerIntTest(
    // support classes
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val testSetupHelper: TestSetupHelper
) : ShouldSpec({

    afterEach {
        testSetupHelper.manualSearchClearing()
    }

    context("GET item types matching the search query") {
        should("return status 200 & no more than ten available item types") {
            // data setup
            val project = testSetupHelper.setupProject()

            val amountOfTypes = MAX_HITS + 3
            val itemTypeName = "some name"
            testSetupHelper.setupItemTypes(amountOfTypes, itemTypeName)

            testSetupHelper.manualSearchIndexing()

            // request
            val mvcResult = mockMvc.get("/projects/${project.id}/available") {
                param("name", itemTypeName)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                }
                .andReturn()

            val typeRef = object : TypeReference<CollectionModel<AvailableItemTypeResource>>() {}
            val responseBody = objectMapper.readValue(mvcResult.response.contentAsString, typeRef)

            responseBody.content shouldHaveSize MAX_HITS
            responseBody.content.forAll {
                it.name shouldBe itemTypeName

                it.links shouldHaveSize 3
                it.links.getLink(SUBSET_OF.displayName, DefaultName.READ) shouldNotBe null
                it.links.getLink(ASSIGNED.displayName, DefaultName.CREATE) shouldNotBe null

                val availableItemTypeSearchLink = it.links.getLink(COLLECTION.value(), DefaultName.SEARCH)
                availableItemTypeSearchLink shouldNotBe null
                availableItemTypeSearchLink!!.href shouldEndWith "/projects/${project.id}/available{?name}"
            }
        }

        should("return status 200 & an empty list if no item types are available") {
            // data setup
            val project = testSetupHelper.setupProject()

            testSetupHelper.manualSearchIndexing()

            // request
            val mvcResult = mockMvc.get("/projects/${project.id}/available") {
                param("name", "some name")
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                }
                .andReturn()

            val typeRef = object : TypeReference<CollectionModel<AvailableItemTypeResource>>() {}
            val responseBody = objectMapper.readValue(mvcResult.response.contentAsString, typeRef)

            responseBody.content shouldHaveSize 0
        }

        should("return status 200 & only itemTypes that are not required for the project") {
            // data setup
            val project = testSetupHelper.setupProject()

            val itemTypeName = "some name"
            val type1 = testSetupHelper.setupItemType(itemTypeName)
            val type2 = testSetupHelper.setupItemType(itemTypeName)

            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = type1.id)
            testSetupHelper.manualSearchIndexing()

            // request
            val mvcResult = mockMvc.get("/projects/${project.id}/available") {
                param("name", itemTypeName)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                }
                .andReturn()

            val typeRef = object : TypeReference<CollectionModel<AvailableItemTypeResource>>() {}
            val responseBody = objectMapper.readValue(mvcResult.response.contentAsString, typeRef)

            responseBody.content shouldHaveSize 1
            val returnedType = responseBody.content.iterator().next()
            returnedType.name shouldBe itemTypeName

            val subsetOfItemTypeReadLink = returnedType.links.getLink(SUBSET_OF.displayName, DefaultName.READ)
            subsetOfItemTypeReadLink shouldNotBe null
            subsetOfItemTypeReadLink!!.href shouldEndWith "/item-types/${type2.id}"

            val assignedItemTypeCreateLink = returnedType.links.getLink(ASSIGNED.displayName, DefaultName.CREATE)
            assignedItemTypeCreateLink shouldNotBe null
            assignedItemTypeCreateLink!!.href shouldEndWith "/projects/${project.id}/required/${type2.id}"

            val availableItemTypeSearchLink = returnedType.links.getLink(COLLECTION.value(), DefaultName.SEARCH)
            availableItemTypeSearchLink shouldNotBe null
            availableItemTypeSearchLink!!.href shouldEndWith "/projects/${project.id}/available{?name}"
        }

        should("return status 200 & itemTypes whose name is contained in the search string") {
            // data setup
            val project = testSetupHelper.setupProject()

            val searchString = "name"
            val itemTypeName = "prefix${searchString}suffix"
            val wrongTypeName = searchString.reversed() + "wrong"

            testSetupHelper.setupItemType(wrongTypeName)
            val type = testSetupHelper.setupItemType(itemTypeName)
            testSetupHelper.setupItemType("somethingElse")
            testSetupHelper.manualSearchIndexing()

            // request
            val mvcResult = mockMvc.get("/projects/${project.id}/available") {
                param("name", itemTypeName)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                }
                .andReturn()

            val typeRef = object : TypeReference<CollectionModel<AvailableItemTypeResource>>() {}
            val responseBody = objectMapper.readValue(mvcResult.response.contentAsString, typeRef)

            responseBody.content shouldHaveSize 1
            val returnedType = responseBody.content.iterator().next()
            returnedType.name shouldBe itemTypeName

            val subsetOfItemTypeReadLink = returnedType.links.getLink(SUBSET_OF.displayName, DefaultName.READ)
            subsetOfItemTypeReadLink shouldNotBe null
            subsetOfItemTypeReadLink!!.href shouldEndWith "/item-types/${type.id}"

            val assignedItemTypeCreateLink = returnedType.links.getLink(ASSIGNED.displayName, DefaultName.CREATE)
            assignedItemTypeCreateLink shouldNotBe null
            assignedItemTypeCreateLink!!.href shouldEndWith "/projects/${project.id}/required/${type.id}"

            val availableItemTypeSearchLink = returnedType.links.getLink(COLLECTION.value(), DefaultName.SEARCH)
            availableItemTypeSearchLink shouldNotBe null
            availableItemTypeSearchLink!!.href shouldEndWith "/projects/${project.id}/available{?name}"
        }

        should("return status 200 & itemTypes whose name has a word distance of one to the search string") {
            // data setup
            val project = testSetupHelper.setupProject()

            val itemTypeName = "name"
            val searchString = "nama"
            val wrongTypeName = "nans"

            testSetupHelper.setupItemType(wrongTypeName)
            val type = testSetupHelper.setupItemType(itemTypeName)
            testSetupHelper.setupItemType("somethingElse")
            testSetupHelper.manualSearchIndexing()

            // request
            val mvcResult = mockMvc.get("/projects/${project.id}/available") {
                param("name", searchString)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                }
                .andReturn()

            val typeRef = object : TypeReference<CollectionModel<AvailableItemTypeResource>>() {}
            val responseBody = objectMapper.readValue(mvcResult.response.contentAsString, typeRef)

            responseBody.content shouldHaveSize 1
            val returnedType = responseBody.content.iterator().next()
            returnedType.name shouldBe itemTypeName

            val subsetOfItemTypeReadLink = returnedType.links.getLink(SUBSET_OF.displayName, DefaultName.READ)
            subsetOfItemTypeReadLink shouldNotBe null
            subsetOfItemTypeReadLink!!.href shouldEndWith "/item-types/${type.id}"

            val assignedItemTypeCreateLink = returnedType.links.getLink(ASSIGNED.displayName, DefaultName.CREATE)
            assignedItemTypeCreateLink shouldNotBe null
            assignedItemTypeCreateLink!!.href shouldEndWith "/projects/${project.id}/required/${type.id}"

            val availableItemTypeSearchLink = returnedType.links.getLink(COLLECTION.value(), DefaultName.SEARCH)
            availableItemTypeSearchLink shouldNotBe null
            availableItemTypeSearchLink!!.href shouldEndWith "/projects/${project.id}/available{?name}"
        }

        should("return status 200 & no more than ten available item types") {
            // data setup
            val project = testSetupHelper.setupProject()

            val searchString = "name"
            val similarItemTypeName = "namw"
            val containedItemTypeName = "prefix${searchString}suffix"

            testSetupHelper.setupItemType(containedItemTypeName)
            testSetupHelper.setupItemType(similarItemTypeName)
            testSetupHelper.manualSearchIndexing()

            // request
            val mvcResult = mockMvc.get("/projects/${project.id}/available") {
                param("name", searchString)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                }
                .andReturn()

            val typeRef = object : TypeReference<CollectionModel<AvailableItemTypeResource>>() {}
            val responseBody = objectMapper.readValue(mvcResult.response.contentAsString, typeRef)

            responseBody.content shouldHaveSize 2
            responseBody.content.map { it.name } shouldContainAll listOf(similarItemTypeName, containedItemTypeName)
        }
    }
})
