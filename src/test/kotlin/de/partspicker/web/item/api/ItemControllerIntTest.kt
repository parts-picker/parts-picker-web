package de.partspicker.web.item.api

import de.partspicker.web.common.exceptions.ErrorCode
import de.partspicker.web.item.api.responses.ItemConditionResponse
import de.partspicker.web.item.api.responses.ItemStatusResponse
import io.kotest.core.spec.style.ShouldSpec
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/itemControllerIntTest.sql")
class ItemControllerIntTest(
    private val mockMvc: MockMvc
) : ShouldSpec({

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
                    jsonPath("$._embedded.items", hasSize<Any>(5))
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
})
