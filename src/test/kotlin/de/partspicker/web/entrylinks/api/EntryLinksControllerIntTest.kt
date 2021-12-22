package de.partspicker.web.entrylinks.api

import de.partspicker.web.entrylinks.api.resources.EntryLinksResource
import io.kotest.core.spec.style.ShouldSpec
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
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
                    jsonPath("$._links.*", hasSize<Any>(2))
                    jsonPath("$._links.${EntryLinksResource.ITEMS_RELATION}", notNullValue())
                    jsonPath("$._links.${EntryLinksResource.ITEM_TYPES_RELATION}", notNullValue())
                }
        }
    }
})
