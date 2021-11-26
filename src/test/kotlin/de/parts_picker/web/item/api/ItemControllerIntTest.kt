package de.parts_picker.web.item.api

import de.parts_picker.web.common.exceptions.ErrorCode
import de.parts_picker.web.item.api.responses.ItemConditionResponse
import de.parts_picker.web.item.api.responses.ItemStatusResponse
import io.kotest.core.spec.style.ShouldSpec
import org.hamcrest.Matchers.*
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
                        jsonPath("$._embedded.itemResourceList", hasSize<Any>(3))
                        jsonPath("$._links", notNullValue())
                    }
        }

    }

})
