package de.partspicker.web.inventory.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.common.exceptions.ErrorCode
import de.partspicker.web.inventory.api.requests.RequiredItemTypePostRequest
import de.partspicker.web.inventory.api.resources.RequiredItemTypeResource.Companion.collectionRelationName
import io.kotest.core.spec.style.ShouldSpec
import org.hamcrest.Matchers.aMapWithSize
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
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/requiredItemTypeControllerIntTest.sql")
class RequiredItemTypeControllerIntTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper
) : ShouldSpec({

    context("POST required item type") {
        should("return status 200 & the resource with the newly created required item type when called") {
            val body = RequiredItemTypePostRequest(
                itemTypeId = 3,
                requiredAmount = 4
            )

            mockMvc.post("/projects/2/required") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(body)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(3))
                    jsonPath("$.itemTypeName", `is`("A nail"))
                    jsonPath("$.requiredAmount", `is`(4))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$._links.assignedTo") { endsWith("/projects/2") }
                    jsonPath("$._links.describedBy") { endsWith("/item-types/3") }
                }
        }

        should("return status 404 when no project with the requested id exists") {
            val nonExistentId = 666L
            val path = "/projects/$nonExistentId/required"

            val body = RequiredItemTypePostRequest(
                itemTypeId = 3,
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
                        jsonPath("$.message", `is`("Project with id $nonExistentId could not be found"))
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 404 when no itemType with the requested id exists") {
            val path = "/projects/2/required"

            val body = RequiredItemTypePostRequest(
                itemTypeId = 666,
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
                                "ItemType with id ${body.itemTypeId} could not be found"
                            )
                        )
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }

        should("return status 422 when requiredAmount is smaller than one") {
            val path = "/projects/2/required"

            val body = RequiredItemTypePostRequest(
                itemTypeId = 3,
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
    }

    context("GET all required item types by projectId") {
        should("return status 200 & all required item types assigned to the project with the given id when called") {
            mockMvc.get("/projects/1/required")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(3))
                    jsonPath("$._embedded.$collectionRelationName", hasSize<Any>(3))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(20))
                    jsonPath("$.page.totalPages", `is`(1))
                    jsonPath("$.page.totalElements", `is`(3))
                    jsonPath("$.page.number", `is`(0))
                }
        }

        should("return status 200 & no required item types when called with project without assigned item types") {
            mockMvc.get("/projects/2/required")
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
})
