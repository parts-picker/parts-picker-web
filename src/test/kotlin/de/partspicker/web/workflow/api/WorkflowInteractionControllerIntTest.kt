package de.partspicker.web.workflow.api

import de.partspicker.web.common.exceptions.ErrorCode
import de.partspicker.web.workflow.api.resources.EdgeInfoResource
import io.kotest.core.spec.style.ShouldSpec
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
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
@Sql("classpath:/init-sql/workflowInteractionControllerIntTest.sql")
class WorkflowInteractionControllerIntTest(
    private val mockMvc: MockMvc
) : ShouldSpec({

    context("GET current node info by instance") {
        should("return status 200 & the resource with the node info belonging to the requested id when called") {
            val id = 1

            mockMvc.get("/instance/$id/node")
                .andExpect {
                    status { isOk() }
                    content {
                        contentType("application/hal+json")
                        jsonPath("$.*", hasSize<Any>(3))
                        jsonPath("$.name", `is`("planning"))
                        jsonPath("$.displayName", `is`("Planning"))
                        jsonPath("$._links", notNullValue())
                        jsonPath("$._links.options.href", endsWith("/instance/1/edges"))
                    }
                }
        }

        should("return status 204 when called & no node is assigned to the instance with the given id") {
            val id = 4

            mockMvc.get("/instance/$id/node")
                .andExpect {
                    status { isNoContent() }
                }
        }

        should("return status 404 when no instance with the requested id exists") {
            val nonExistentId = 666

            val path = "/instance/$nonExistentId/node"

            mockMvc.get(path)
                .andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("$.*", hasSize<Any>(7))
                        jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                        jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                        jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                        jsonPath(
                            "$.message",
                            `is`("Workflow instance with id $nonExistentId could not be found")
                        )
                        jsonPath("$.path", `is`(path))
                        jsonPath("$.timestamp", notNullValue())
                    }
                }
        }
    }

    context("GET all possible edges by instance id") {
        should("return status 200 & all edgeInfos belonging to the given instance id") {
            mockMvc.get("/instance/1/edges")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(2))
                    jsonPath("$._embedded.${EdgeInfoResource.collectionRelationName}", hasSize<Any>(1))
                    jsonPath(
                        "$._embedded.${EdgeInfoResource.collectionRelationName}[*].name",
                        containsInAnyOrder("planning_to_implementation")
                    )
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 200 & no edgeInfos when called with instance id with a stop node") {
            mockMvc.get("/instance/5/edges")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(1))
                    jsonPath("$._embedded.${EdgeInfoResource.collectionRelationName}") { doesNotHaveJsonPath() }
                    jsonPath("$._links", notNullValue())
                }
        }
    }

    context("POST instance state advance") {
        should("return status 200 & the new current node info") {
            mockMvc.post("/instance/1/edges/1")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(3))
                    jsonPath("$.name", `is`("implementation"))
                    jsonPath("$.displayName", `is`("Implementation"))
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 404 when given non-existent instance id") {
            val nonExistentId = 666L
            val path = "/instance/$nonExistentId/edges/1"

            mockMvc.post(path)
                .andExpect {
                    status { isNotFound() }
                    jsonPath("$.*", hasSize<Any>(7))
                    jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                    jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                    jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                    jsonPath(
                        "$.message",
                        `is`("Workflow instance with id $nonExistentId could not be found")
                    )
                    jsonPath("$.path", `is`(path))
                    jsonPath("$.timestamp", notNullValue())
                }
        }

        should("return status 422 when given inactive instance") {
            val instanceId = 4L
            val path = "/instance/$instanceId/edges/1"

            mockMvc.post(path)
                .andExpect {
                    status { isUnprocessableEntity() }
                    jsonPath("$.*", hasSize<Any>(7))
                    jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                    jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                    jsonPath("$.errorCode") { nullValue() }
                    jsonPath(
                        "$.message",
                        `is`("Workflow instance with id $instanceId cannot be edited because it is inactive.")
                    )
                    jsonPath("$.path", `is`(path))
                    jsonPath("$.timestamp", notNullValue())
                }
        }

        should("return status 404 when given non-existent edge id") {
            val nonExistentId = 666L
            val path = "/instance/1/edges/$nonExistentId"

            mockMvc.post(path)
                .andExpect {
                    status { isNotFound() }
                    jsonPath("$.*", hasSize<Any>(7))
                    jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.name))
                    jsonPath("$.statusCode", `is`(HttpStatus.NOT_FOUND.value()))
                    jsonPath("$.errorCode", `is`(ErrorCode.EntityNotFound.code))
                    jsonPath(
                        "$.message",
                        `is`("Workflow edge with id $nonExistentId could not be found")
                    )
                    jsonPath("$.path", `is`(path))
                    jsonPath("$.timestamp", notNullValue())
                }
        }

        should("return status 422 when current node & edge source node not matching") {
            val edgeId = 3L
            val path = "/instance/1/edges/$edgeId"

            mockMvc.post(path)
                .andExpect {
                    status { isUnprocessableEntity() }
                    jsonPath("$.*", hasSize<Any>(7))
                    jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                    jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                    jsonPath("$.errorCode") { nullValue() }
                    jsonPath(
                        "$.message",
                        `is`(
                            "The current instance node with id 1 does not match the source node with " +
                                "id 3 of the given edge with id $edgeId to advance the instance state"
                        )
                    )
                    jsonPath("$.path", `is`(path))
                    jsonPath("$.timestamp", notNullValue())
                }
        }
    }
})
