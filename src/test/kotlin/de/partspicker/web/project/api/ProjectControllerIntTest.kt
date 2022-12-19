package de.partspicker.web.project.api

import de.partspicker.web.common.exceptions.ErrorCode
import de.partspicker.web.project.api.resources.ProjectResource
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
@Sql("classpath:/init-sql/projectControllerIntTest.sql")
class ProjectControllerIntTest(
    private val mockMvc: MockMvc
) : ShouldSpec({

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
})
