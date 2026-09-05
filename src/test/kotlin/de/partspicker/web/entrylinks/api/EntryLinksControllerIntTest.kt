package de.partspicker.web.entrylinks.api

import de.partspicker.web.orgunit.api.resources.OrgUnitResource
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import org.hamcrest.Matchers.hasSize
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("integration")
class EntryLinksControllerIntTest(
    private val mockMvc: MockMvc
) : ShouldSpec({

    context("GET entryLinks") {

        should("return status 200 & all entryLinks") {
            mockMvc.get("/entry")
                .andExpect {
                    status { isOk() }
                    content { contentType("application/hal+json") }
                    jsonPath("$.*", hasSize<Any>(1))
                    jsonPath("$._links.*", hasSize<Any>(1))
                    jsonPath("$._links.${OrgUnitResource.COLLECTION_RELATION_NAME}", hasSize<Any>(2))
                }
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
