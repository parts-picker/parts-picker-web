package de.partspicker.web.project.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.partspicker.web.common.exceptions.ErrorCode
import de.partspicker.web.common.exceptions.ErrorDetail
import de.partspicker.web.project.api.requests.ProjectDescriptionPatchRequest
import de.partspicker.web.project.api.requests.ProjectMetaInfoPatchRequest
import de.partspicker.web.project.api.requests.ProjectPostRequest
import de.partspicker.web.project.api.resources.ProjectResource
import de.partspicker.web.test.util.TestSetupHelper
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
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
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/projectControllerIntTest.sql")
class ProjectControllerIntTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    private val testSetupHelper: TestSetupHelper
) : ShouldSpec({

    context("POST project") {
        should("return status 200 & the resource with the newly created project when called") {
            val postRequestBody = ProjectPostRequest(
                name = "Project name",
                shortDescription = "Project description",
                groupId = 1L
            )

            mockMvc.post("/projects") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(postRequestBody)
            }.andExpect {
                status { isOk() }
                content {
                    contentType("application/hal+json")
                    jsonPath("$.*", hasSize<Any>(8))
                    jsonPath("$.id", notNullValue())
                    jsonPath("$.name", `is`(postRequestBody.name))
                    jsonPath("$.status", `is`("planning"))
                    jsonPath("$.displayStatus", `is`("Planning"))
                    jsonPath("$.shortDescription", `is`(postRequestBody.shortDescription))
                    jsonPath("$.description", nullValue())
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
                    jsonPath("$._embedded.${ProjectResource.collectionRelationName}", hasSize<Any>(4))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(20))
                    jsonPath("$.page.totalPages", `is`(1))
                    jsonPath("$.page.totalElements", `is`(4))
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
                    jsonPath("$._embedded.${ProjectResource.collectionRelationName}", hasSize<Any>(2))
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page.size", `is`(size))
                    jsonPath("$.page.totalPages", `is`(2))
                    jsonPath("$.page.totalElements", `is`(4))
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
                        jsonPath("$.*", hasSize<Any>(8))
                        jsonPath("$.id", `is`(id))
                        jsonPath("$.name", `is`("PROJECT 1"))
                        jsonPath("$.status", `is`("start"))
                        jsonPath("$.displayStatus", `is`("Start"))
                        jsonPath("$.shortDescription", `is`("Description for project 1"))
                        jsonPath("$.description", nullValue())
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
    }

    context("UPDATE project meta info") {
        should("return status 200 & the updated project when called") {
            val requestBody = ProjectMetaInfoPatchRequest(
                name = "Updated name",
                shortDescription = "Updated description",
                groupId = 2
            )

            val id = 3

            mockMvc.patch("/projects/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(requestBody)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(8))
                    jsonPath("$.id", `is`(id))
                    jsonPath("$.name", `is`(requestBody.name))
                    jsonPath("$.status", `is`("start"))
                    jsonPath("$.displayStatus", `is`("Start"))
                    jsonPath("$.shortDescription", `is`(requestBody.shortDescription))
                    jsonPath("$.description", nullValue())
                    jsonPath("$.groupId", `is`(2))
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 404 when no project with the requested id exists") {
            val requestBody = ProjectMetaInfoPatchRequest(
                name = "Updated name",
                shortDescription = "Updated description",
                groupId = null
            )

            val nonExistentId = 666
            val path = "/projects/$nonExistentId"

            mockMvc.patch(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(requestBody)
            }
                .andExpect {
                    status { isNotFound() }
                    content { contentType(MediaType.APPLICATION_JSON) }
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

    context("UPDATE project description") {
        should("return status 200 & the updated project when called") {
            // data setup
            val project = testSetupHelper.setupProject()

            // request
            val requestBody = ProjectDescriptionPatchRequest(
                description = "Updated description",
            )

            val mvcResult = mockMvc.patch("/projects/${project.id}") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(requestBody)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(8))
                }
                .andReturn()

            val responseResource = mapper.readValue<ProjectResource>(mvcResult.response.contentAsString)

            responseResource.id shouldBe project.id
            responseResource.name shouldBe project.name
            responseResource.status shouldBe project.status
            responseResource.displayStatus shouldBe project.displayStatus
            responseResource.shortDescription shouldBe project.shortDescription
            responseResource.description shouldBe requestBody.description
            responseResource.groupId shouldBe project.group?.id
            responseResource.links shouldNotBe null
        }

        should("return status 422 when given description with size bigger than max allowed size") {
            // data setup
            val project = testSetupHelper.setupProject()

            // request
            val requestBody = ProjectDescriptionPatchRequest(
                description = Arb.string(ProjectDescriptionPatchRequest.MAX_DESCRIPTION_SIZE + 1).single(),
            )

            val path = "/projects/${project.id}"

            mockMvc.patch(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(requestBody)
            }
                .andExpect {
                    status { isUnprocessableEntity() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.*", hasSize<Any>(7))
                    jsonPath("$.status", `is`(HttpStatus.UNPROCESSABLE_ENTITY.name))
                    jsonPath("$.statusCode", `is`(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                    jsonPath(
                        "$.message",
                        `is`("Validation for object projectPatchRequest failed with 1 error(s)")
                    )
                    jsonPath<Collection<ErrorDetail>>("$.errors", hasSize(1))
                    jsonPath("$.errors[0].key", `is`("description"))
                    jsonPath(
                        "$.errors[0].message",
                        `is`(ProjectDescriptionPatchRequest.MAX_DESCRIPTION_SIZE_MESSAGE)
                    )
                    jsonPath("$.path", `is`(path))
                    jsonPath("$.timestamp", notNullValue())
                }
        }

        should("return status 404 when no project with the requested id exists") {
            val requestBody = ProjectDescriptionPatchRequest(
                description = "Updated description",
            )

            val nonExistentId = 666
            val path = "/projects/$nonExistentId"

            mockMvc.patch(path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(requestBody)
            }
                .andExpect {
                    status { isNotFound() }
                    content { contentType(MediaType.APPLICATION_JSON) }
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

    context("DELETE project") {
        should("return status 204 when called & successfully deleted") {
            // data setup
            val project = testSetupHelper.setupProject()

            // request
            mockMvc.delete("/projects/${project.id}")
                .andExpect {
                    status { isNoContent() }
                }
        }

        should("return status 404 when called & no project with the requested id exists") {
            val nonExistentId = 666
            val path = "/projects/$nonExistentId"

            mockMvc.delete(path)
                .andExpect {
                    status { isNotFound() }
                    content { contentType(MediaType.APPLICATION_JSON) }
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
}) {
    override fun extensions() = listOf(SpringExtension)
}
