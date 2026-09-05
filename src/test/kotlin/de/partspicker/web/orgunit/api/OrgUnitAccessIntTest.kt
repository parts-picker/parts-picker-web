package de.partspicker.web.orgunit.api

import de.partspicker.web.test.util.TestUsers
import de.partspicker.web.test.util.UnrelatedOrgUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional

/**
 * Requests made as [TestUsers] against [UnrelatedOrgUnit], which they are no member of.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("integration")
@Transactional
@Sql("classpath:/init-sql/testUser.sql", "classpath:/init-sql/unrelatedOrgUnit.sql")
class OrgUnitAccessIntTest(
    private val mockMvc: MockMvc
) : ShouldSpec({

    context("requests against an unrelated org unit") {
        should("return status 403 for its items") {
            mockMvc.get("/org-units/${UnrelatedOrgUnit.ID}/items")
                .andExpect { status { isForbidden() } }
        }

        should("return status 403 for its item types") {
            mockMvc.get("/org-units/${UnrelatedOrgUnit.ID}/item-types")
                .andExpect { status { isForbidden() } }
        }

        should("return status 403 for its projects") {
            mockMvc.get("/org-units/${UnrelatedOrgUnit.ID}/projects")
                .andExpect { status { isForbidden() } }
        }

        should("return status 403 for its groups") {
            mockMvc.get("/org-units/${UnrelatedOrgUnit.ID}/groups")
                .andExpect { status { isForbidden() } }
        }

        should("return status 403 for the org unit itself") {
            mockMvc.get("/org-units/${UnrelatedOrgUnit.ID}")
                .andExpect { status { isForbidden() } }
        }

        should("return status 403 for one of its items") {
            mockMvc.get("/items/${UnrelatedOrgUnit.ITEM_ID}")
                .andExpect { status { isForbidden() } }
        }

        should("return status 403 for one of its item types") {
            mockMvc.get("/item-types/${UnrelatedOrgUnit.ITEM_TYPE_ID}")
                .andExpect { status { isForbidden() } }
        }

        should("return status 403 for the items of one of its item types") {
            mockMvc.get("/item-types/${UnrelatedOrgUnit.ITEM_TYPE_ID}/items")
                .andExpect { status { isForbidden() } }
        }

        should("return status 403 for one of its groups") {
            mockMvc.get("/groups/${UnrelatedOrgUnit.GROUP_ID}")
                .andExpect { status { isForbidden() } }
        }

        should("return status 403 when deleting one of its items") {
            mockMvc.delete("/items/${UnrelatedOrgUnit.ITEM_ID}")
                .andExpect { status { isForbidden() } }
        }

        should("return status 403 when deleting one of its item types") {
            mockMvc.delete("/item-types/${UnrelatedOrgUnit.ITEM_TYPE_ID}")
                .andExpect { status { isForbidden() } }
        }

        should("return status 200 for the same listing in the callers own org unit") {
            mockMvc.get("/org-units/${TestUsers.ORG_UNIT_ID}/items")
                .andExpect { status { isOk() } }
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
