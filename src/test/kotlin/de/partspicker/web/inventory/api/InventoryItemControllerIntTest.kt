package de.partspicker.web.inventory.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.common.hal.DefaultName
import de.partspicker.web.common.hal.RelationName.ASSIGNED_TO
import de.partspicker.web.common.hal.RelationName.SUBSET_OF
import de.partspicker.web.inventory.api.resources.AssignableItemResource
import de.partspicker.web.inventory.api.resources.AssignedItemResource
import de.partspicker.web.inventory.api.responses.InventoryItemConditionResponse
import de.partspicker.web.item.business.ItemService
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import de.partspicker.web.test.util.TestSetupHelper
import de.partspicker.web.test.util.getLink
import de.partspicker.web.workflow.business.WorkflowInteractionService
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldEndWith
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
class InventoryItemControllerIntTest(
    // support classes
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val testSetupHelper: TestSetupHelper,
    private val itemService: ItemService,
    private val workflowInteractionService: WorkflowInteractionService
) : ShouldSpec({
    context("GET all assignable items") {
        should("return status 200 & the resource with all assignable items for the given type & project") {
            // data setup
            val project = testSetupHelper.setupProject()
            val otherProject = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()

            val alreadyAssignedAmount = 2
            testSetupHelper.setupRequiredItemType(
                projectId = project.id,
                itemTypeId = itemType.id,
                alreadyAssignedAmount + 1L
            )

            val assignableAmount = 6
            testSetupHelper.setupItemsForType(
                amountToCreate = alreadyAssignedAmount,
                itemType = itemType,
                projectId = project.id
            )

            testSetupHelper.setupItemsForType(amountToCreate = assignableAmount, itemType = itemType)

            val unusableAmount = 9
            testSetupHelper.setupItemsForType(
                amountToCreate = unusableAmount,
                itemType = itemType,
                condition = ItemCondition.REPAIRABLE
            )

            val wrongStatusAmount = 4
            testSetupHelper.setupItemsForType(
                amountToCreate = wrongStatusAmount,
                itemType = itemType,
                status = ItemStatus.ORDERED
            )

            val alreadyAssignedToDifferentProjectAmount = 3
            testSetupHelper.setupRequiredItemType(
                projectId = otherProject.id,
                itemTypeId = itemType.id,
                alreadyAssignedToDifferentProjectAmount.toLong()
            )
            testSetupHelper.setupItemsForType(
                amountToCreate = alreadyAssignedToDifferentProjectAmount,
                itemType = itemType,
                otherProject.id
            )

            // request
            val mvcResult = mockMvc.get("/projects/${project.id}/required/${itemType.id}/assignable")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                }
                .andReturn()

            val typeRef = object : TypeReference<PagedModel<AssignableItemResource>>() {}
            val responseBody = objectMapper.readValue(mvcResult.response.contentAsString, typeRef)

            responseBody.metadata!!.size shouldBe 20
            responseBody.metadata!!.totalPages shouldBe 1
            responseBody.metadata!!.totalElements shouldBe assignableAmount
            responseBody.metadata!!.number shouldBe 0

            responseBody.content shouldHaveSize assignableAmount
            responseBody.content.forAll {
                it.condition shouldBe InventoryItemConditionResponse.NEW

                it.links shouldHaveSize 2
                it.links.getLink(SUBSET_OF.displayName, DefaultName.READ) shouldNotBe null

                val assignedToUpdateLink = it.links.getLink(ASSIGNED_TO.displayName, DefaultName.UPDATE)
                assignedToUpdateLink shouldNotBe null
                assignedToUpdateLink!!.href shouldEndWith "/assigned/${project.id}"
            }
        }
    }

    context("GET all assigned items") {
        should("return status 200 & the resource with all assigned items for the given type & project") {
            // data setup
            val project = testSetupHelper.setupProject()
            val otherProject = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()

            val assignedAmount = 15
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id, 15)
            testSetupHelper.setupItemsForType(
                amountToCreate = assignedAmount,
                itemType = itemType,
                projectId = project.id
            )

            val assignableAmount = 6
            testSetupHelper.setupItemsForType(amountToCreate = assignableAmount, itemType = itemType)

            val alreadyAssignedToDifferentProjectAmount = 3
            testSetupHelper.setupRequiredItemType(
                projectId = otherProject.id,
                itemTypeId = itemType.id,
                alreadyAssignedToDifferentProjectAmount.toLong()
            )
            testSetupHelper.setupItemsForType(
                amountToCreate = alreadyAssignedToDifferentProjectAmount,
                itemType = itemType,
                otherProject.id
            )

            // request
            val mvcResult = mockMvc.get("/projects/${project.id}/required/${itemType.id}/assigned")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                }
                .andReturn()

            val typeRef = object : TypeReference<PagedModel<AssignedItemResource>>() {}
            val responseBody = objectMapper.readValue(mvcResult.response.contentAsString, typeRef)

            responseBody.metadata!!.size shouldBe 20
            responseBody.metadata!!.totalPages shouldBe 1
            responseBody.metadata!!.totalElements shouldBe assignedAmount
            responseBody.metadata!!.number shouldBe 0

            responseBody.content shouldHaveSize assignedAmount
            responseBody.content.forAll {
                it.condition shouldBe InventoryItemConditionResponse.NEW
                it.links.hasSize(3) shouldBe true

                it.links.getLink(SUBSET_OF.displayName, DefaultName.READ) shouldNotBe null

                val assignedToReadLink = it.links.getLink(ASSIGNED_TO.displayName, DefaultName.READ)
                assignedToReadLink shouldNotBe null
                assignedToReadLink!!.href shouldEndWith "/projects/${project.id}"

                val assignedToUpdateLink = it.links.getLink(ASSIGNED_TO.displayName, DefaultName.UPDATE)
                assignedToUpdateLink shouldNotBe null
                assignedToUpdateLink!!.href shouldEndWith "/assignable"
            }
        }
    }

    context("PATCH assign item to project") {
        should("return status 204") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)

            val item = testSetupHelper.setupItemForType(itemType)

            // request
            mockMvc.patch("/items/${item.id}/assigned/${project.id}")
                .andExpect {
                    status { isNoContent() }
                }

            val patchedItem = itemService.getItemById(item.id)
            patchedItem.assignedProjectId shouldBe project.id
        }

        should("return status 422 when project status not 'planning'") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)
            workflowInteractionService.forceInstanceNode(project.workflowInstanceId, "implementation")

            val item = testSetupHelper.setupItemForType(itemType)

            // request
            val path = "/items/${item.id}/assigned/${project.id}"
            mockMvc.patch(path)
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath(
                            "$.message",
                            `is`(
                                "Expected project status is planning, but actual status is implementation"
                            )
                        )
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }

            val patchedItem = itemService.getItemById(item.id)
            patchedItem.assignedProjectId shouldBe null
        }

        should("return status 422 when required amount exceeded") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)
            testSetupHelper.setupItemForType(itemType = itemType, projectId = project.id)

            val item = testSetupHelper.setupItemForType(itemType)

            // request
            val path = "/items/${item.id}/assigned/${project.id}"
            mockMvc.patch(path)
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath(
                            "$.message",
                            `is`("The required amount 1 is smaller than or equal to the assigned amount")
                        )
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }

            val patchedItem = itemService.getItemById(item.id)
            patchedItem.assignedProjectId shouldBe null
        }

        should("return status 422 when item has not status 'IN_STOCK'") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)

            val item = testSetupHelper.setupItemForType(itemType, status = ItemStatus.USED)

            // request
            val path = "/items/${item.id}/assigned/${project.id}"
            val message = "Status of item with id ${item.id} must be IN_STOCK to be assignable but was ${item.status}"
            mockMvc.patch(path)
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath("$.message", `is`(message))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }

            val patchedItem = itemService.getItemById(item.id)
            patchedItem.assignedProjectId shouldBe null
        }

        should("return status 422 when given already assigned item") {
            // data setup
            val project = testSetupHelper.setupProject()
            val otherProject = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)

            val item = testSetupHelper.setupItemForType(itemType, projectId = otherProject.id)

            // request
            val path = "/items/${item.id}/assigned/${project.id}"
            val message =
                "Item with id '${item.id}' may not be assigned to another project while it is already assigned"
            mockMvc.patch(path)
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath("$.message", `is`(message))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }

            val patchedItem = itemService.getItemById(item.id)
            patchedItem.assignedProjectId shouldBe otherProject.id
        }
    }

    context("PATCH unassign item") {
        should("return status 204") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)

            val item = testSetupHelper.setupItemForType(itemType, projectId = project.id)

            // request
            mockMvc.patch("/items/${item.id}/assignable")
                .andExpect {
                    status { isNoContent() }
                }

            val patchedItem = itemService.getItemById(item.id)
            patchedItem.assignedProjectId shouldBe null
        }

        should("return status 422 when project status not 'planning'") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id)
            workflowInteractionService.forceInstanceNode(project.workflowInstanceId, "implementation")

            val item = testSetupHelper.setupItemForType(itemType, projectId = project.id)

            // request
            val path = "/items/${item.id}/assignable"
            val message = "Expected project status is planning, but actual status is implementation"
            mockMvc.patch(path)
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath("$.message", `is`(message))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }

            val patchedItem = itemService.getItemById(item.id)
            patchedItem.assignedProjectId shouldBe project.id
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
