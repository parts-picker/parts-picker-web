package de.partspicker.web.workflow.api

import de.partspicker.web.common.exceptions.ErrorCode
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
})
