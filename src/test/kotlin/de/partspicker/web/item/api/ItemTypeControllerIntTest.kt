package de.partspicker.web.item.api

import de.partspicker.web.common.exceptions.ErrorCode
import de.partspicker.web.item.api.resources.ItemTypeResource
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
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/itemTypeControllerIntTest.sql")
class ItemTypeControllerIntTest(
    private val mockMvc: MockMvc
) : ShouldSpec({

    context("GET itemType") {

        should("return status 200 & the resource with the item belonging to the requested id when called") {
            val id = 2

            mockMvc.get("/item-types/$id")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(3))
                    jsonPath("$.name", `is`("A screw"))
                    jsonPath("$.description", `is`("A small screw"))
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 404 when no itemType with the requested id exists") {
            val nonExistentId = 666
            val path = "/item-types/$nonExistentId"

            mockMvc.get(path)
                .andExpect {
                    status { isNotFound() }
                    content { contentType(MediaType.APPLICATION_JSON) }
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

    context("GET all itemTypes") {

        should("return status 200 & all items when called") {
            mockMvc.get("/item-types")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(2))
                    jsonPath("$._embedded", notNullValue())
                    jsonPath("$._embedded.${ItemTypeResource.collectionRelationName}", hasSize<Any>(3))
                    jsonPath("$._links", notNullValue())
                }
        }
    }
})
