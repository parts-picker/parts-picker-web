package de.partspicker.web.inventory.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.common.exceptions.ErrorCode
import de.partspicker.web.inventory.api.requests.RequiredItemTypePatchRequest
import de.partspicker.web.inventory.api.requests.RequiredItemTypePostRequest
import de.partspicker.web.inventory.api.resources.RequiredItemTypeResource.Companion.collectionRelationName
import de.partspicker.web.inventory.business.InventoryItemService
import de.partspicker.web.test.util.TestSetupHelper
import de.partspicker.web.workflow.business.WorkflowInteractionService
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.comparables.shouldBeLessThan
import org.hamcrest.Matchers.aMapWithSize
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
class RequiredItemTypeControllerIntTest(
    // support classes
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    private val workflowInteractionService: WorkflowInteractionService,
    private val inventoryItemService: InventoryItemService,
    private val testSetupHelper: TestSetupHelper
) : ShouldSpec({
    context("POST required item type") {
        should("return status 200 & the resource with the newly created required item type when called") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemTypeName = "A nail"
            val itemType = testSetupHelper.setupItemType(itemTypeName)

            // request
            val body = RequiredItemTypePostRequest(
                requiredAmount = 4
            )

            mockMvc.post("/projects/${project.id}/required/${itemType.id}") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(4))
                    jsonPath("$.itemTypeName", `is`(itemTypeName))
                    jsonPath("$.assignedAmount", `is`(0))
                    jsonPath("$.requiredAmount", `is`(4))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$._links.assignedTo") { endsWith("/projects/${project.id}") }
                    jsonPath("$._links.describedBy") { endsWith("/item-types/${itemType.id}") }
                }
        }

        should("return status 404 when no project with the requested id exists") {
            val nonExistentProjectId = 666L
            val itemTypeId = 3L
            val path = "/projects/$nonExistentProjectId/required/$itemTypeId"

            val body = RequiredItemTypePostRequest(
                requiredAmount = 4
            )

            mockMvc.post(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                        jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                        jsonPath("$.message", `is`("Project with id $nonExistentProjectId could not be found"))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 404 when no itemType with the requested id exists") {
            // data setup
            val project = testSetupHelper.setupProject()

            // request
            val nonExistentItemType = 666L
            val path = "/projects/${project.id}/required/$nonExistentItemType"

            val body = RequiredItemTypePostRequest(
                requiredAmount = 4
            )

            mockMvc.post(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                        jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                        jsonPath(
                            "$.message",
                            `is`(
                                "ItemType with id $nonExistentItemType could not be found"
                            )
                        )
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 422 when requested requiredAmount is smaller than one") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()

            // request
            val path = "/projects/${project.id}/required/${itemType.id}"

            val body = RequiredItemTypePostRequest(
                requiredAmount = 0
            )

            mockMvc.post(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath(
                            "$.message",
                            `is`(
                                "Validation for object requiredItemTypePostRequest failed with 1 error(s)"
                            )
                        )
                        jsonPath<Map<out String, String>>("$.errors", aMapWithSize(1))
                        jsonPath("$.errors.requiredAmount", `is`("must be greater than or equal to 1"))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 422 when requested requiredAmount is smaller than assignedAmount") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()

            val assignedAmount = 4
            val requiredAmount = 1L
            // checking test fail condition
            requiredAmount shouldBeLessThan assignedAmount.toLong()

            testSetupHelper.setupItemsForType(assignedAmount, itemType, project.id)

            // request
            val path = "/projects/${project.id}/required/${itemType.id}"

            val body = RequiredItemTypePostRequest(
                requiredAmount = requiredAmount
            )

            val expectedMessage =
                "The required amount $requiredAmount is smaller than the assigned amount $assignedAmount"

            mockMvc.post(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath("$.message", `is`(expectedMessage))
                        jsonPath<Map<out String, String>>("$.errors", aMapWithSize(1))
                        jsonPath(
                            "$.errors.RequiredItemTypeAmountSmallerThanAssignedException",
                            `is`(expectedMessage)
                        )
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 422 when project status does not equal 'planning'") {
            // data setup
            val project = testSetupHelper.setupProject()
            workflowInteractionService.forceInstanceNode(project.workflowInstanceId, "implementation")
            val itemType = testSetupHelper.setupItemType()

            // request
            val path = "/projects/${project.id}/required/${itemType.id}"

            val body = RequiredItemTypePostRequest(
                requiredAmount = 1L
            )

            val expectedMessage = "Expected project status is planning, but actual status is implementation"

            mockMvc.post(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath("$.message", `is`(expectedMessage))
                        jsonPath<Map<out String, String>>("$.errors", aMapWithSize(1))
                        jsonPath("$.errors.WrongNodeNameRuleException", `is`(expectedMessage))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }
    }

    context("GET all required item types by projectId") {
        should("return status 200 & all required item types assigned to the project with the given id when called") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemTypeAmount = 5
            repeat(itemTypeAmount) {
                val itemType = testSetupHelper.setupItemType()
                testSetupHelper.setupRequiredItemType(project.id, itemType.id)
            }

            // request
            mockMvc.get("/projects/${project.id}/required")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(3))
                    jsonPath("$._embedded.$collectionRelationName", hasSize<Any>(itemTypeAmount))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(20))
                    jsonPath("$.page.totalPages", `is`(1))
                    jsonPath("$.page.totalElements", `is`(itemTypeAmount))
                    jsonPath("$.page.number", `is`(0))
                }
        }

        should("return status 200 & no required item types when called with project without assigned item types") {
            // data setup
            val project = testSetupHelper.setupProject()

            // request
            mockMvc.get("/projects/${project.id}/required")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(2))
                    jsonPath("$._embedded") { doesNotHaveJsonPath() }
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(20))
                    jsonPath("$.page.totalPages", `is`(0))
                    jsonPath("$.page.totalElements", `is`(0))
                    jsonPath("$.page.number", `is`(0))
                }
        }

        should("return status 200 & no required item types when called with non-existent project") {
            mockMvc.get("/projects/666/required")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(2))
                    jsonPath("$._embedded") { doesNotHaveJsonPath() }
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(20))
                    jsonPath("$.page.totalPages", `is`(0))
                    jsonPath("$.page.totalElements", `is`(0))
                    jsonPath("$.page.number", `is`(0))
                }
        }
    }

    context("PATCH requiredItemType by projectId & itemTypeId") {
        should("return status 200 & the updated required item type when called") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemTypeName = "Small 8 Ohm Speaker"
            val itemType = testSetupHelper.setupItemType(itemTypeName)
            testSetupHelper.setupRequiredItemType(project.id, itemType.id, 1L)

            // request
            val requiredAmount = 5
            val patchRequestBody = RequiredItemTypePatchRequest(requiredAmount.toLong())

            mockMvc.patch("/projects/${project.id}/required/${itemType.id}") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(patchRequestBody)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(4))
                    jsonPath("$.itemTypeName", `is`(itemTypeName))
                    jsonPath("$.assignedAmount", `is`(0))
                    jsonPath("$.requiredAmount", `is`(requiredAmount))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$._links.assignedTo") { endsWith("/projects/${project.id}") }
                    jsonPath("$._links.describedBy") { endsWith("/item-types/${itemType.id}") }
                }
        }

        should("return status 404 when no project with the requested id exists") {
            val nonExistentId = 666L
            val path = "/projects/$nonExistentId/required/1"

            val body = RequiredItemTypePatchRequest(5)

            mockMvc.patch(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                        jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                        jsonPath("$.message", `is`("Project with id $nonExistentId could not be found"))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 404 when no itemType with the requested id exists") {
            // data setup
            val project = testSetupHelper.setupProject()

            // request
            val nonExistentItemTypeId = 666
            val path = "/projects/${project.id}/required/666"
            val body = RequiredItemTypePatchRequest(5)

            val expectedMessage = "ItemType with id $nonExistentItemTypeId could not be found"

            mockMvc.patch(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                        jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                        jsonPath("$.message", `is`(expectedMessage))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 422 when updated requiredAmount is smaller than one") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(project.id, itemType.id, 1L)

            // request
            val path = "/projects/${project.id}/required/${itemType.id}"
            val body = RequiredItemTypePatchRequest(0)

            val expectedMessage = "Validation for object requiredItemTypePatchRequest failed with 1 error(s)"

            mockMvc.patch(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath("$.message", `is`(expectedMessage))
                        jsonPath<Map<out String, String>>("$.errors", aMapWithSize(1))
                        jsonPath("$.errors.requiredAmount", `is`("must be greater than or equal to 1"))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 422 when updated requiredAmount is smaller than assignedAmount") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()

            val assignedAmount = 4
            val requiredAmount = 1L
            // checking test fail condition
            requiredAmount shouldBeLessThan assignedAmount.toLong()

            testSetupHelper.setupItemsForType(assignedAmount, itemType, project.id)

            // request
            val path = "/projects/${project.id}/required/${itemType.id}"

            val body = RequiredItemTypePostRequest(
                requiredAmount = requiredAmount
            )

            val expectedMessage =
                "The required amount $requiredAmount is smaller than the assigned amount $assignedAmount"

            mockMvc.patch(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath("$.message", `is`(expectedMessage))
                        jsonPath<Map<out String, String>>("$.errors", aMapWithSize(1))
                        jsonPath(
                            "$.errors.RequiredItemTypeAmountSmallerThanAssignedException",
                            `is`(expectedMessage)
                        )
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 422 when project status does not equal 'planning'") {
            // data setup
            val project = testSetupHelper.setupProject()
            workflowInteractionService.forceInstanceNode(project.workflowInstanceId, "implementation")
            val itemType = testSetupHelper.setupItemType()

            // request
            val path = "/projects/${project.id}/required/${itemType.id}"

            val body = RequiredItemTypePostRequest(
                requiredAmount = 1L
            )

            val expectedMessage = "Expected project status is planning, but actual status is implementation"

            mockMvc.patch(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath("$.message", `is`(expectedMessage))
                        jsonPath<Map<out String, String>>("$.errors", aMapWithSize(1))
                        jsonPath("$.errors.WrongNodeNameRuleException", `is`(expectedMessage))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }
    }

    context("DELETE requiredItemType") {
        should("return status 204 when called & successfully deleted") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            val itemAmount = 4
            testSetupHelper.setupRequiredItemType(project.id, itemType.id, itemAmount.toLong())
            testSetupHelper.setupItemsForType(itemAmount, itemType, project.id)

            // request
            mockMvc.delete("/projects/${project.id}/required/${itemType.id}")
                .andExpect {
                    status { isNoContent() }
                }

            val assignedItems = inventoryItemService.readAllAssignedForItemTypeAndProject(
                itemTypeId = itemType.id,
                projectId = project.id,
                Pageable.unpaged()
            )

            assignedItems.shouldBeEmpty()
        }

        should("return status 404 when called & no project with the requested id exists") {
            val nonExistentProjectId = 666L
            val path = "/projects/$nonExistentProjectId/required/1"

            mockMvc.delete(path)
                .andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                        jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                        jsonPath(
                            "$.message",
                            `is`(
                                "Project with id $nonExistentProjectId could not be found"
                            )
                        )
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 404 when called & no itemType with the requested id exists") {
            // data setup
            val project = testSetupHelper.setupProject()

            // request
            val nonExistentItemTypeId = 666L
            val path = "/projects/${project.id}/required/$nonExistentItemTypeId"

            mockMvc.delete(path)
                .andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                        jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                        jsonPath(
                            "$.message",
                            `is`(
                                "ItemType with id $nonExistentItemTypeId could not be found"
                            )
                        )
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 422 when called & project status is not 'planning'") {
            // data setup
            val project = testSetupHelper.setupProject()
            val itemType = testSetupHelper.setupItemType()
            testSetupHelper.setupRequiredItemType(project.id, itemType.id, 1L)

            workflowInteractionService.forceInstanceNode(project.workflowInstanceId, "implementation")

            // request
            val path = "/projects/${project.id}/required/${itemType.id}"

            val expectedMessage = "Expected project status is planning, but actual status is implementation"

            mockMvc.delete(path)
                .andExpect {
                    status { isUnprocessableEntity() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                        jsonPath("$.message", `is`(expectedMessage))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }
    }
})
