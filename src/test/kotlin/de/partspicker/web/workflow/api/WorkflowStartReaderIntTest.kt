package de.partspicker.web.workflow.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.workflow.business.WorkflowService
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

@JsonTest
class WorkflowStartReaderIntTest(
    private val objectMapper: ObjectMapper
) : ShouldSpec({

    val ressources = arrayOf<Resource>(ClassPathResource("test_workflows/testworkflow.json"))
    val path = "test_workflows"
    val workflowServiceMock = mockk<WorkflowService>()
    val cut = WorkflowStartupReader(
        resources = ressources,
        path = path,
        objectMapper = objectMapper,
        workflowService = workflowServiceMock
    )

    afterTest {
        clearMocks(workflowServiceMock)
    }

    context("read, parse & create workflow") {
        should("read the workflow & pass it to create the workflow") {
            // given
            // data is read from test_workflows
            every { workflowServiceMock.exists("Testflows", 1L) } returns false
            every { workflowServiceMock.latestVersion("Testflows") } returns null
            every { workflowServiceMock.create(any()) } returns Unit

            // when
            cut.onApplicationEvent(null)

            // then
            verify {
                workflowServiceMock.create(
                    match { workflowCreate ->
                        workflowCreate.name == "Testflows" &&
                            workflowCreate.version == 1L &&
                            workflowCreate.nodes.size == 3 &&
                            workflowCreate.edges.size == 2
                    }
                )
            }
        }

        should("not create a new workflow if workflow with same name & version already exists") {
            // given
            // data is read from test_workflows
            every { workflowServiceMock.exists("Testflows", 1L) } returns true

            // when
            cut.onApplicationEvent(null)

            // then
            verify(exactly = 0) {
                workflowServiceMock.create(
                    any()
                )
            }
        }

        should("not create a new workflow if workflow with same name & newer version already exists") {
            // given
            // data is read from test_workflows
            every { workflowServiceMock.exists("Testflows", 1L) } returns false
            every { workflowServiceMock.latestVersion("Testflows") } returns 10

            // when
            cut.onApplicationEvent(null)

            // then
            verify(exactly = 0) {
                workflowServiceMock.create(
                    any()
                )
            }
        }
    }
})
