package de.partspicker.web.item.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.common.exceptions.ErrorCode
import de.partspicker.web.item.api.requests.ItemConditionRequest
import de.partspicker.web.item.api.requests.ItemGeneralPatchRequest
import de.partspicker.web.item.api.requests.ItemPostRequest
import de.partspicker.web.item.api.requests.ItemProjectPatchRequest
import de.partspicker.web.item.api.requests.ItemStatusRequest
import de.partspicker.web.item.api.resources.ItemResource
import de.partspicker.web.item.api.responses.ItemConditionResponse
import de.partspicker.web.item.api.responses.ItemStatusResponse
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import io.kotest.core.spec.style.ShouldSpec
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
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
@Sql("classpath:/init-sql/itemControllerIntTest.sql")
class ItemControllerIntTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper
) : ShouldSpec({

    context("POST item") {
        should("return status 200 & the resource with the newly created item when called") {
            val body = ItemPostRequest(
                1,
                ItemStatusRequest.IN_STOCK,
                ItemConditionRequest.NEW,
                note = "A newly created item"
            )

            mockMvc.post("/item-types/4/items") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(5))
                    jsonPath("$.id", notNullValue())
                    jsonPath("$.status", `is`(body.status.name))
                    jsonPath("$.condition", `is`(body.condition.name))
                    jsonPath("$.note", `is`(body.note))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$._links.assignedTo.href", endsWith("projects/${body.assignedProjectId}"))
                }
        }

        should("return status 404 when no itemType with the requested id exists") {
            val nonExistentId = 666L
            val path = "/item-types/$nonExistentId/items"

            val postRequestBody = ItemPostRequest(
                null,
                ItemStatusRequest.IN_STOCK,
                ItemConditionRequest.NEW,
                note = "A newly created item"
            )

            mockMvc.post(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(postRequestBody)
            }
                .andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                        jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                        jsonPath("$.message", `is`("ItemType with id $nonExistentId could not be found"))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }
    }

    context("GET item") {

        should("return status 200 & the resource with the item belonging to the requested id when called") {
            val id = 4

            mockMvc.get("/items/$id")
                .andExpect {
                    status { isOk() }
                    content {
                        contentType("application/hal+json")
                        jsonPath("$.*", hasSize<Any>(5))
                        jsonPath("$.id", `is`(id))
                        jsonPath("$.status", `is`(ItemStatusResponse.IN_STOCK.name))
                        jsonPath("$.condition", `is`(ItemConditionResponse.USED.name))
                        jsonPath("$.note", `is`("Salvaged Speaker"))
                        jsonPath("$._links", notNullValue())
                        jsonPath("$._links.assignedTo") { doesNotHaveJsonPath() }
                    }
                }
        }

        should("return status 404 when no item with the requested id exists") {
            val nonExistentId = 666
            val path = "/items/$nonExistentId"

            mockMvc.get(path)
                .andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                        jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                        jsonPath("$.message", `is`("Item with id $nonExistentId could not be found"))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }
    }

    context("GET all items") {

        should("return status 200 & all items when called") {
            mockMvc.get("/items")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(3))
                    jsonPath("$._embedded.${ItemResource.collectionRelationName}", hasSize<Any>(6))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(20))
                    jsonPath("$.page.totalPages", `is`(1))
                    jsonPath("$.page.totalElements", `is`(6))
                    jsonPath("$.page.number", `is`(0))
                }
        }

        should("return status 200 & all items on the specified page when called") {
            val size = 2
            val page = 1

            mockMvc.get("/items?page=$page&size=$size")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(3))
                    jsonPath("$._embedded.${ItemResource.collectionRelationName}", hasSize<Any>(2))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(size))
                    jsonPath("$.page.totalPages", `is`(3))
                    jsonPath("$.page.totalElements", `is`(6))
                    jsonPath("$.page.number", `is`(page))
                }
        }
    }

    context("GET all items by itemTypeId") {

        should("return status 200 & all items belonging to the type with the given id when called") {
            mockMvc.get("/item-types/3/items")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(3))
                    jsonPath("$._embedded.${ItemResource.collectionRelationName}", hasSize<Any>(2))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(20))
                    jsonPath("$.page.totalPages", `is`(1))
                    jsonPath("$.page.totalElements", `is`(2))
                    jsonPath("$.page.number", `is`(0))
                }
        }

        should("return status 200 & no items when called with itemType without linked items") {
            mockMvc.get("/item-types/2/items")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(2))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(20))
                    jsonPath("$.page.totalPages", `is`(0))
                    jsonPath("$.page.totalElements", `is`(0))
                    jsonPath("$.page.number", `is`(0))
                }
        }

        should("return status 200 & no items when called with non-existent itemType") {
            mockMvc.get("/item-types/666/items")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(2))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(20))
                    jsonPath("$.page.totalPages", `is`(0))
                    jsonPath("$.page.totalElements", `is`(0))
                    jsonPath("$.page.number", `is`(0))
                }
        }
    }

    context("UPDATE item") {
        context("general fields body") {
            should("return status 200 & the updated item when called") {
                val patchRequestBody = ItemGeneralPatchRequest(
                    ItemConditionRequest.NEW,
                    "The updated note"
                )

                mockMvc.patch("/items/7") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(patchRequestBody)
                }
                    .andExpect {
                        status { isOk() }
                        content { contentType("application/hal+json") }
                        jsonPath("$.*", hasSize<Any>(5))
                        jsonPath("$.id", `is`(7))
                        jsonPath("$.status", `is`(ItemStatus.IN_STOCK.name))
                        jsonPath("$.condition", `is`(ItemCondition.NEW.name))
                        jsonPath("$.note", `is`(patchRequestBody.note))
                        jsonPath("$._links", notNullValue())
                        jsonPath("$._links.assignedTo") { notNullValue() }
                    }
            }

            should("return status 404 when no item with the requested id exists") {
                val putRequestBody = ItemGeneralPatchRequest(
                    ItemConditionRequest.NEW,
                    "The updated note"
                )

                val nonExistentId = 666
                val path = "/items/$nonExistentId"

                mockMvc.patch(path) {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(putRequestBody)
                }
                    .andExpect {
                        status { isNotFound() }
                        content { contentType(MediaType.APPLICATION_JSON) }
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                        jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                        jsonPath("$.message", `is`("Item with id $nonExistentId could not be found"))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
            }
        }

        context("assigned projectId body") {
            should("return status 200 & the updated item when called") {
                val body = ItemProjectPatchRequest(
                    assignedProjectId = 1L
                )

                mockMvc.patch("/items/7") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(body)
                }
                    .andExpect {
                        status { isOk() }
                        content { contentType("application/hal+json") }
                        jsonPath("$.*", hasSize<Any>(5))
                        jsonPath("$.id", `is`(7))
                        jsonPath("$.status", `is`(ItemStatus.IN_STOCK.name))
                        jsonPath("$.condition", `is`(ItemCondition.USED.name))
                        jsonPath("$.note", `is`("ITEM ONE"))
                        jsonPath("$._links", notNullValue())
                        jsonPath("$._links.assignedTo.href") { endsWith("projects/${body.assignedProjectId}") }
                    }
            }

            should("return status 404 when no item with the requested id exists") {
                val putRequestBody = ItemProjectPatchRequest(
                    assignedProjectId = 1L
                )

                val nonExistentId = 666
                val path = "/items/$nonExistentId"

                mockMvc.patch(path) {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(putRequestBody)
                }
                    .andExpect {
                        status { isNotFound() }
                        content { contentType(MediaType.APPLICATION_JSON) }
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                        jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                        jsonPath("$.message", `is`("Item with id $nonExistentId could not be found"))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
            }

            should("return status 404 when no project with the given id exists") {
                val nonExistentId = 666L
                val putRequestBody = ItemProjectPatchRequest(
                    assignedProjectId = nonExistentId
                )
                val path = "/items/7"

                mockMvc.patch(path) {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(putRequestBody)
                }
                    .andExpect {
                        status { isNotFound() }
                        content { contentType(MediaType.APPLICATION_JSON) }
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
    }

    context("DELETE item") {

        should("return status 204 when called & successfully delete the item belonging to the given id") {
            mockMvc.delete("/items/9")
                .andExpect {
                    status { isNoContent() }
                }
        }

        should("return status 404 when called & no item with the requested id exists") {
            val nonExistentId = 666
            val path = "/items/$nonExistentId"

            mockMvc.delete(path)
                .andExpect {
                    status { isNotFound() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.*", hasSize<Any>(7))
                    jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                    jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                    jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                    jsonPath("$.message", `is`("Item with id $nonExistentId could not be found"))
                    jsonPath("$.path", `is`(path))
                    jsonPath("$.timestamp", notNullValue())
                }
        }
    }
})
