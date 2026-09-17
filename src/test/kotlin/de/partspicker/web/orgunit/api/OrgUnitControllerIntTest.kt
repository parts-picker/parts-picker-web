package de.partspicker.web.orgunit.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.item.api.resources.ItemResource
import de.partspicker.web.item.api.resources.ItemTypeResource
import de.partspicker.web.orgunit.api.requests.OrgUnitPostRequest
import de.partspicker.web.orgunit.api.resources.OrgUnitMembershipResource
import de.partspicker.web.project.api.resources.GroupResource
import de.partspicker.web.project.api.resources.ProjectResource
import de.partspicker.web.test.util.TestUsers
import de.partspicker.web.test.util.UnrelatedOrgUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
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
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/testUser.sql", "classpath:/init-sql/unrelatedOrgUnit.sql")
class OrgUnitControllerIntTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper
) : ShouldSpec({

    context("POST orgUnit") {

        should("return status 200 & the resource with the newly created org unit when called") {
            val postRequestBody = OrgUnitPostRequest(
                name = "test name",
                shortDescription = "test description"
            )

            mockMvc.post("/org-units") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(postRequestBody)
            }
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.name", `is`(postRequestBody.name))
                    jsonPath("$.shortDescription", `is`(postRequestBody.shortDescription))
                    jsonPath("$.owner.id", `is`(TestUsers.ID.toInt()))
                    jsonPath("$.createdBy.id", `is`(TestUsers.ID.toInt()))
                    jsonPath("$.createdOn", notNullValue())
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 409 when an org unit with the given name already exists") {
            val postRequestBody = OrgUnitPostRequest(
                name = "Test Workshop",
                shortDescription = null
            )

            mockMvc.post("/org-units") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(postRequestBody)
            }
                .andExpect {
                    status { isConflict() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.status", `is`(HttpStatus.CONFLICT.name))
                }
        }
    }

    context("GET orgUnit") {

        should("return status 200 & the resource with the org unit belonging to the requested id when called") {
            mockMvc.get("/org-units/${TestUsers.ORG_UNIT_ID}")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.name", `is`("Test Workshop"))
                    jsonPath("$.shortDescription", `is`("The org unit integration test fixtures live in"))
                    jsonPath("$.owner.id", `is`(TestUsers.ID.toInt()))
                    jsonPath("$.createdBy.id", `is`(TestUsers.ID.toInt()))
                    jsonPath("$.createdOn", notNullValue())
                    jsonPath("$._links", notNullValue())
                }
        }

        should("return status 403 when the caller holds nothing in the requested org unit") {
            mockMvc.get("/org-units/${UnrelatedOrgUnit.ID}")
                .andExpect {
                    status { isForbidden() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.status", `is`(HttpStatus.FORBIDDEN.name))
                }
        }
    }

    context("GET all org unit memberships of the current user") {
        val embedded = "$._embedded.${OrgUnitMembershipResource.COLLECTION_RELATION_NAME}"

        should("return status 200 & one membership per org unit the caller is a member of when called") {
            mockMvc.get("/org-unit-memberships")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath(embedded, hasSize<Any>(1))
                    jsonPath("$embedded[0].orgUnit.name", `is`("Test Workshop"))
                    jsonPath(
                        "$embedded[0].orgUnit.shortDescription",
                        `is`("The org unit integration test fixtures live in")
                    )
                    jsonPath("$embedded[0].accessLevel", `is`("MAINTAIN"))
                    jsonPath("$embedded[0].joinedOn", notNullValue())
                    jsonPath("$._links", notNullValue())
                    jsonPath("$.page", notNullValue())
                }
        }

        should("return the links needed to work in an org unit without reading it in full") {
            mockMvc.get("/org-unit-memberships")
                .andExpect {
                    status { isOk() }
                    jsonPath("$embedded[0]._links.self", notNullValue())
                    jsonPath("$embedded[0]._links.${ProjectResource.COLLECTION_RELATION_NAME}", notNullValue())
                    jsonPath("$embedded[0]._links.${ItemTypeResource.COLLECTION_RELATION_NAME}", notNullValue())
                    jsonPath("$embedded[0]._links.${ItemResource.COLLECTION_RELATION_NAME}", notNullValue())
                    jsonPath("$embedded[0]._links.${GroupResource.COLLECTION_RELATION_NAME}", notNullValue())
                }
        }

        should("not return a membership for an org unit the caller is no member of") {
            mockMvc.get("/org-unit-memberships")
                .andExpect {
                    status { isOk() }
                    jsonPath("$embedded[?(@.orgUnit.name == 'Unrelated Workshop')]", hasSize<Any>(0))
                }
        }

        should("sort by the fields of the membership resource") {
            mockMvc.post("/org-units") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(
                    OrgUnitPostRequest(name = "Another Workshop", shortDescription = null)
                )
            }.andExpect { status { isOk() } }

            mockMvc.get("/org-unit-memberships?sort=orgUnit.name,desc")
                .andExpect {
                    status { isOk() }
                    jsonPath(embedded, hasSize<Any>(2))
                    jsonPath("$embedded[0].orgUnit.name", `is`("Test Workshop"))
                    jsonPath("$embedded[1].orgUnit.name", `is`("Another Workshop"))
                }

            mockMvc.get("/org-unit-memberships?sort=orgUnit.name,asc")
                .andExpect {
                    status { isOk() }
                    jsonPath("$embedded[0].orgUnit.name", `is`("Another Workshop"))
                    jsonPath("$embedded[1].orgUnit.name", `is`("Test Workshop"))
                }
        }

        should("return status 400 when sorting by an unknown field") {
            mockMvc.get("/org-unit-memberships?sort=name,asc")
                .andExpect {
                    status { isBadRequest() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.status", `is`(HttpStatus.BAD_REQUEST.name))
                    jsonPath("$.message", `is`("Unknown property 'name'"))
                }
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
