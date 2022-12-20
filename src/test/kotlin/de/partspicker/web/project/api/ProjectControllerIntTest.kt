package de.partspicker.web.project.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.common.exceptions.ErrorCode
import de.partspicker.web.project.api.requests.ProjectPostRequest
import de.partspicker.web.project.api.requests.ProjectPutRequest
import de.partspicker.web.project.api.resources.ProjectResource
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
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/projectControllerIntTest.sql")
class ProjectControllerIntTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper
) : ShouldSpec({

    context("POST project") {
        should("return status 200 & the resource with the newly created project when called") {
            val postRequestBody = ProjectPostRequest(
                name = "Project name",
                description = "Project description",
                groupId = 1L
            )

            mockMvc.post("/projects") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(postRequestBody)
            }.andExpect {
                status { isOk() }
                content {
                    contentType("application/hal+json")
                    jsonPath("$.*", hasSize<Any>(5))
                    jsonPath("$.id", notNullValue())
                    jsonPath("$.name", `is`(postRequestBody.name))
                    jsonPath("$.description", `is`(postRequestBody.description))
                    jsonPath("$.groupId", `is`(1))
                    jsonPath("$._links", notNullValue())
                }
            }
        }
    }

    context("GET all projects") {
        should("return status 200 & all projects when called") {
            mockMvc.get("/projects")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(3))
                    jsonPath("$._embedded.${ProjectResource.collectionRelationName}", hasSize<Any>(3))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(20))
                    jsonPath("$.page.totalPages", `is`(1))
                    jsonPath("$.page.totalElements", `is`(3))
                    jsonPath("$.page.number", `is`(0))
                }
        }

        should("return status 200 & all projects on the specified page when called") {
            val size = 2
            val page = 1

            mockMvc.get("/projects?=page=$page&size=$size")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(3))
                    jsonPath("$._embedded.${ProjectResource.collectionRelationName}", hasSize<Any>(1))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(size))
                    jsonPath("$.page.totalPages", `is`(2))
                    jsonPath("$.page.totalElements", `is`(3))
                    jsonPath("$.page.number", `is`(page))
                }
        }
    }

    context("GET project") {
        should("return status 200 & the resource with the project belonging to the requested id when called") {
            val id = 1

            mockMvc.get("/projects/$id")
                .andExpect {
                    status { isOk() }
                    content {
                        contentType("application/hal+json")
                        jsonPath("$.*", hasSize<Any>(5))
                        jsonPath("$.id", `is`(id))
                        jsonPath("$.name", `is`("PROJECT 1"))
                        jsonPath("$.description", `is`("Description for project 1"))
                        jsonPath("$.groupId", `is`(1))
                        jsonPath("$._links", notNullValue())
                    }
                }
        }

        should("return status 404 when no project with the requested id exists") {
            val nonExistentId = 666
            val path = "/projects/$nonExistentId"

            mockMvc.get(path)
                .andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("$.*", hasSize<Any>(6))
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

    context("UPDATE project") {
        should("return status 200 & the updated item when called") {
            val putRequestBody = ProjectPutRequest(
                name = "Updated name",
                description = "Updated description",
                groupId = 2
            )

            val id = 3

            mockMvc.put("/projects/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(putRequestBody)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(5))
                    jsonPath("$.id", `is`(id))
                    jsonPath("$.name", `is`(putRequestBody.name))
                    jsonPath("$.description", `is`(putRequestBody.description))
                    jsonPath("$.groupId", `is`(2))
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 404 when no project with the requested id exists") {
            val putRequestBody = ProjectPutRequest(
                name = "Updated name",
                description = "Updated description",
                groupId = null
            )

            val nonExistentId = 666
            val path = "/projects/$nonExistentId"

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
                    jsonPath("$.message", `is`("Project with id $nonExistentId could not be found"))
                    jsonPath("$.path", `is`(path))
                    jsonPath("$.timestamp", notNullValue())
                }
        }
    }
})
