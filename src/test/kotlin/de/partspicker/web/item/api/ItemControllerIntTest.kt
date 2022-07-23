package de.partspicker.web.item.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.common.exceptions.ErrorCode
import de.partspicker.web.item.api.requests.ItemConditionRequest
import de.partspicker.web.item.api.requests.ItemPostRequest
import de.partspicker.web.item.api.requests.ItemPutRequest
import de.partspicker.web.item.api.requests.ItemStatusRequest
import de.partspicker.web.item.api.responses.ItemConditionResponse
import de.partspicker.web.item.api.responses.ItemStatusResponse
import de.partspicker.web.item.business.objects.enums.ItemCondition
import de.partspicker.web.item.business.objects.enums.ItemStatus
import io.kotest.core.spec.style.ShouldSpec
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
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
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

        should("return status 200 & the resource with the newly create item when called") {
            val postRequestBody = ItemPostRequest(
                typeId = 4,
                ItemStatusRequest.IN_STOCK,
                ItemConditionRequest.NEW,
                note = "A newly created item"
            )

            mockMvc.post("/items") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(postRequestBody)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(5))
                    jsonPath("$.id", notNullValue())
                    jsonPath("$.status", `is`(postRequestBody.status.name))
                    jsonPath("$.condition", `is`(postRequestBody.condition.name))
                    jsonPath("$.note", `is`(postRequestBody.note))
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 404 & when no itemType with the requested id exists") {
            val nonExistentId = 666L
            val path = "/items"

            val postRequestBody = ItemPostRequest(
                typeId = nonExistentId,
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
                        jsonPath("$.*", hasSize<Any>(6))
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
                        jsonPath("$.*", hasSize<Any>(6))
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
                    jsonPath("$.*", hasSize<Any>(2))
                    jsonPath("$._embedded", notNullValue())
                    jsonPath("$._embedded.items", hasSize<Any>(6))
                    jsonPath("$._links", notNullValue())
                }
        }
    }

    context("GET all items by itemTypeId") {

        should("return status 200 & all items belonging to the type with the given id when called") {
            mockMvc.get("/item-types/3/items")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(2))
                    jsonPath("$._embedded", notNullValue())
                    jsonPath("$._embedded.items", hasSize<Any>(2))
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 200 & no items when called with itemType without linked items") {
            mockMvc.get("/item-types/2/items")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(1))
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 200 & no items when called with non-existent itemType") {
            mockMvc.get("/item-types/666/items")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(1))
                    jsonPath("$._links", notNullValue())
                }
        }
    }

    context("UPDATE item") {

        should("return status 200 & the updated item when called") {
            val putRequestBody = ItemPutRequest(
                ItemStatusRequest.ORDERED,
                ItemConditionRequest.NEW,
                "The updated note"
            )

            mockMvc.put("/items/7") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(putRequestBody)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(5))
                    jsonPath("$.status", `is`(ItemStatus.ORDERED.name))
                    jsonPath("$.condition", `is`(ItemCondition.NEW.name))
                    jsonPath("$.note", `is`(putRequestBody.note))
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 404 when no item with the requested id exists") {
            val putRequestBody = ItemPutRequest(
                ItemStatusRequest.ORDERED,
                ItemConditionRequest.NEW,
                "The updated note"
            )

            val nonExistentId = 666
            val path = "/items/$nonExistentId"

            mockMvc.put(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(putRequestBody)
            }
                .andExpect {
                    status { isNotFound() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.*", hasSize<Any>(6))
                    jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                    jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                    jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                    jsonPath("$.message", `is`("Item with id $nonExistentId could not be found"))
                    jsonPath("$.path", `is`(path))
                    jsonPath("$.timestamp", notNullValue())
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
                    jsonPath("$.*", hasSize<Any>(6))
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
