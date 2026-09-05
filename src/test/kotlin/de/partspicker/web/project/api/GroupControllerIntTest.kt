package de.partspicker.web.project.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.common.exceptions.ErrorCode
import de.partspicker.web.project.api.requests.GroupPostRequest
import de.partspicker.web.project.api.requests.GroupPutRequest
import de.partspicker.web.project.api.resources.GroupResource
import de.partspicker.web.test.util.TestUsers
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
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
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/testUser.sql", "classpath:/init-sql/groupControllerIntTest.sql")
class GroupControllerIntTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper
) : ShouldSpec({

    context("POST group") {

        should("return status 201 & the resource with the newly created group when called") {
            val postRequestBody = GroupPostRequest(
                name = "test name",
                description = "test description"
            )

            mockMvc.post("/org-units/${TestUsers.ORG_UNIT_ID}/groups") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(postRequestBody)
            }
                .andExpect {
                    status { isCreated() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(4))
                    jsonPath("$.id", notNullValue())
                    jsonPath("$.name", `is`(postRequestBody.name))
                    jsonPath("$.description", `is`(postRequestBody.description))
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 403 when the caller holds nothing in the requested org unit") {
            val nonExistentId = 666
            val postRequestBody = GroupPostRequest(name = "test name", description = null)

            mockMvc.post("/org-units/$nonExistentId/groups") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(postRequestBody)
            }
                .andExpect {
                    status { isForbidden() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.status", `is`(HttpStatus.FORBIDDEN.name))
                }
        }
    }

    context("GET group") {

        should("return status 200 & the resource with the group belonging to the requested id when called") {
            val id = 1

            mockMvc.get("/groups/$id")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(4))
                    jsonPath("$.id", `is`(id))
                    jsonPath("$.name", `is`("GROUP 1"))
                    jsonPath("$.description", `is`("Description for group 1"))
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 404 when no group with the requested id exists") {
            val nonExistentId = 666

            mockMvc.get("/groups/$nonExistentId")
                .andExpect {
                    status { isNotFound() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                    jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                }
        }
    }

    context("GET all groups") {

        should("return status 200 & the resource with all groups of the requested org unit when called") {
            mockMvc.get("/org-units/${TestUsers.ORG_UNIT_ID}/groups")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$._embedded.${GroupResource.COLLECTION_RELATION_NAME}", hasSize<Any>(3))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page", notNullValue())
                }
        }

        should("return status 403 when the caller holds nothing in the requested org unit") {
            val nonExistentId = 666

            mockMvc.get("/org-units/$nonExistentId/groups")
                .andExpect {
                    status { isForbidden() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.status", `is`(HttpStatus.FORBIDDEN.name))
                }
        }
    }

    context("PUT group") {

        should("return status 200 & the resource with the updated group when called") {
            val id = 2
            val putRequestBody = GroupPutRequest(
                name = "updated name",
                description = null
            )

            mockMvc.put("/groups/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(putRequestBody)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.id", `is`(id))
                    jsonPath("$.name", `is`(putRequestBody.name))
                    jsonPath("$.description", nullValue())
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 404 when no group with the requested id exists") {
            val nonExistentId = 666
            val putRequestBody = GroupPutRequest(name = "updated name", description = null)

            mockMvc.put("/groups/$nonExistentId") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(putRequestBody)
            }
                .andExpect {
                    status { isNotFound() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                    jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                }
        }
    }

    context("DELETE group") {

        should("return status 204 & remove the group when called") {
            val id = 3

            mockMvc.delete("/groups/$id")
                .andExpect { status { isNoContent() } }

            mockMvc.get("/groups/$id")
                .andExpect { status { isNotFound() } }
        }

        should("return status 404 when no group with the requested id exists") {
            val nonExistentId = 666

            mockMvc.delete("/groups/$nonExistentId")
                .andExpect {
                    status { isNotFound() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                    jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                }
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
