package de.partspicker.web.inventory.api

import de.partspicker.web.inventory.api.resources.RequiredItemTypeResource.Companion.collectionRelationName
import io.kotest.core.spec.style.ShouldSpec
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/requiredItemTypeControllerIntTest.sql")
class RequiredItemTypeControllerIntTest(
    private val mockMvc: MockMvc
) : ShouldSpec({

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
