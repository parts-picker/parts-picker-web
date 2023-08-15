package de.partspicker.web.workflow.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.partspicker.web.workflow.business.WorkflowService
import de.partspicker.web.workflow.business.exceptions.WorkflowAlreadyExistsException
import de.partspicker.web.workflow.business.exceptions.WorkflowLatestVersionIsGreaterException
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

@JsonTest
class WorkflowStartupReaderUnitTest(
    private val objectMapper: ObjectMapper
) : ShouldSpec({

    val resources = arrayOf<Resource>(ClassPathResource("test_workflows/testworkflow.json"))
    val path = "test_workflows"
    val workflowServiceMock = mockk<WorkflowService>()
    val cut = WorkflowStartupReader(
        resources = resources,
        path = path,
        objectMapper = objectMapper,
        workflowService = workflowServiceMock
    )

    afterTest {
        clearMocks(workflowServiceMock)
    }

    context("read, parse & create workflow") {
        should("read the workflow json & pass it to create the workflow") {
            // given
            // data is read from test_workflows
            every { workflowServiceMock.readLatest(any()) } returns null
            every { workflowServiceMock.create(any()) } returns mockk()

            // when
            cut.onApplicationEvent(null)

            // then
            verify {
                workflowServiceMock.create(
                    match { workflowCreate ->
                        workflowCreate.name shouldBe "Testflows"
                        workflowCreate.version shouldBe 1L
                        workflowCreate.nodes shouldHaveSize 5
                        workflowCreate.edges shouldHaveSize 4

                        true
                    }
                )
            }
        }

        should("handle WorkflowAlreadyExitsException gracefully by skipping the workflow") {
            // given
            // data is read from test_workflows
            every { workflowServiceMock.readLatest(any()) } returns null
            every { workflowServiceMock.create(any()) } throws WorkflowAlreadyExistsException("name", 1L)

            // when
            cut.onApplicationEvent(null)

            // then
            verify { workflowServiceMock.create(any()) }
        }

        should("handle WorkflowLatestVersionIsGreaterException gracefully by skipping the workflow") {
            // given
            // data is read from test_workflows
            every { workflowServiceMock.readLatest(any()) } returns null
            every { workflowServiceMock.create(any()) } throws WorkflowLatestVersionIsGreaterException("name", 3L, 2L)

            // when
            cut.onApplicationEvent(null)

            // then
            verify { workflowServiceMock.create(any()) }
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
