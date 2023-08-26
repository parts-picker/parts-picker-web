package de.partspicker.web.project.business

import de.partspicker.web.project.business.exceptions.GroupNotFoundException
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.business.objects.CreateProject
import de.partspicker.web.project.business.objects.Project
import de.partspicker.web.project.persistance.GroupRepository
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.project.persistance.entities.ProjectEntity
import de.partspicker.web.test.generators.ProjectEntityGenerators
import de.partspicker.web.test.generators.id
import de.partspicker.web.workflow.business.WorkflowInteractionService
import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.persistence.InstanceRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.single
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.Optional

class ProjectServiceUnitTest : ShouldSpec({
    val projectRepositoryMock = mockk<ProjectRepository>()
    val groupRepositoryMock = mockk<GroupRepository>()
    val workflowInteractionServiceMock = mockk<WorkflowInteractionService>()
    val instanceRepositoryMock = mockk<InstanceRepository>()
    val cut = ProjectService(
        projectRepository = projectRepositoryMock,
        groupRepository = groupRepositoryMock,
        workflowInteractionService = workflowInteractionServiceMock,
        instanceRepository = instanceRepositoryMock
    )

    afterTest {
        clearMocks(projectRepositoryMock)
    }

    context("create") {
        should("create new project & return it") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { groupRepositoryMock.existsById(projectEntity.group?.id!!) } returns true
            every { projectRepositoryMock.save(any()) } returns projectEntity
            every { workflowInteractionServiceMock.startProjectWorkflow() } returns
                Instance.from(projectEntity.workflowInstance!!)
            every { instanceRepositoryMock.getReferenceById(any()) } returns mockk()

            // when
            val returnedProject = cut.create(
                CreateProject(
                    name = projectEntity.name,
                    description = projectEntity.description,
                    groupId = projectEntity.group?.id
                )
            )

            verify(exactly = 1) {
                projectRepositoryMock.save(any())
                workflowInteractionServiceMock.startProjectWorkflow()
            }

            returnedProject shouldBe Project.from(projectEntity)
        }

        should("throw GroupNotFoundException when given non-existent group") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { groupRepositoryMock.existsById(projectEntity.group?.id!!) } returns false

            // when
            val exception = shouldThrow<GroupNotFoundException> {
                cut.create(
                    CreateProject(
                        name = projectEntity.name,
                        description = projectEntity.description,
                        groupId = projectEntity.group?.id
                    )
                )
            }

            // then
            exception.message shouldBe "Group with id ${projectEntity.group?.id!!} could not be found"
        }
    }

    context("readAll") {
        should("return all items") {
            // given
            val projectsPage: Page<ProjectEntity> = PageImpl(
                listOf(
                    ProjectEntityGenerators.generator.next(),
                    ProjectEntityGenerators.generator.next()
                )
            )
            every { projectRepositoryMock.findAll(Pageable.unpaged()) } returns projectsPage

            // when
            val returnedProjects = cut.readAll(Pageable.unpaged())

            // then
            returnedProjects shouldBe Project.AsPage.from(projectsPage)
        }

        should("return empty list when no projects available") {
            // given
            every { projectRepositoryMock.findAll(Pageable.unpaged()) } returns Page.empty()

            // when
            val returnedProjects = cut.readAll(Pageable.unpaged())

            // then
            returnedProjects shouldBe Page.empty()
        }
    }

    context("read") {
        should("return correct project when given existent id") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)

            // when
            val returnedProject = cut.read(projectEntity.id)

            // then
            returnedProject shouldBe Project.from(projectEntity)
        }

        should("throw ProjectNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { projectRepositoryMock.findById(randomId) } returns Optional.empty()

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.read(randomId)
            }

            // then
            exception.message shouldBe "Project with id $randomId could not be found"
        }
    }

    context("readByInstanceId") {
        should("return correct project when given existent id") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every {
                projectRepositoryMock.findByWorkflowInstanceId(projectEntity.workflowInstance!!.id)
            } returns projectEntity

            // when
            val returnedProject = cut.readByInstanceId(projectEntity.workflowInstance!!.id)

            // then
            returnedProject shouldBe Project.from(projectEntity)
        }

        should("return null when given non-existent id") {
            // given
            val randomId = Arb.id().single()
            every { projectRepositoryMock.findByWorkflowInstanceId(randomId) } returns null

            // when
            val returnedProject = cut.readByInstanceId(randomId)

            // then
            returnedProject shouldBe null
        }
    }

    context("update") {
        should("update the project with the given id with no group & return it") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next().copy(group = null)
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)
            every { projectRepositoryMock.save(projectEntity) } returns projectEntity

            // when
            val updatedProject = cut.update(
                projectId = projectEntity.id,
                name = projectEntity.name!!,
                description = projectEntity.description,
                groupId = null
            )

            // then
            verify(exactly = 1) {
                projectRepositoryMock.save(projectEntity)
            }

            updatedProject.name shouldBe projectEntity.name
            updatedProject.description shouldBe projectEntity.description
        }

        should("update the project with the given id & return it") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)
            every { projectRepositoryMock.save(any()) } returns projectEntity
            every { groupRepositoryMock.existsById(projectEntity.group!!.id) } returns true

            // when
            val updatedProject = cut.update(
                projectId = projectEntity.id,
                name = projectEntity.name!!,
                description = projectEntity.description,
                groupId = projectEntity.group!!.id
            )

            // then
            verify(exactly = 1) {
                projectRepositoryMock.save(any())
                groupRepositoryMock.existsById(projectEntity.group!!.id)
            }

            updatedProject.name shouldBe projectEntity.name
            updatedProject.description shouldBe projectEntity.description
            updatedProject.group!!.id shouldBe projectEntity.group!!.id
            updatedProject.workflowInstanceId shouldBe projectEntity.workflowInstance!!.id
        }

        should("throw ProjectNotFoundException when given non-existent id") {
            // given
            val nonExistentId = 666L
            every { projectRepositoryMock.findById(nonExistentId) } returns Optional.empty()

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.update(
                    projectId = nonExistentId,
                    name = "name",
                    description = "description",
                    groupId = null
                )
            }

            // then
            verify(exactly = 0) {
                projectRepositoryMock.save(any())
            }

            exception.message shouldBe "Project with id $nonExistentId could not be found"
        }

        should("throw GroupNotFoundException when given non-existent group") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            val nonExistentId = 666L
            every { projectRepositoryMock.findById(projectEntity.id) } returns Optional.of(projectEntity)
            every { groupRepositoryMock.existsById(nonExistentId) } returns false

            // when
            val exception = shouldThrow<GroupNotFoundException> {
                cut.update(
                    projectId = projectEntity.id,
                    name = "name",
                    description = "description",
                    groupId = nonExistentId
                )
            }

            // then
            verify(exactly = 0) {
                projectRepositoryMock.save(any())
            }

            exception.message shouldBe "Group with id $nonExistentId could not be found"
        }
    }

    context("delete") {
        should("delete the project with the given id") {
            // given
            val projectId = Arb.long(min = 1).next()

            every { projectRepositoryMock.existsById(projectId) } returns true
            every { projectRepositoryMock.deleteById(projectId) } returns Unit

            // when
            cut.delete(projectId)

            // then
            verify(exactly = 1) {
                projectRepositoryMock.deleteById(projectId)
            }
        }

        should("throw ProjectNotFoundException when given non-existent id") {
            // given
            val projectId = Arb.long(min = 1).next()

            every { projectRepositoryMock.existsById(projectId) } returns false

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.delete(projectId)
            }

            // then
            exception.message shouldBe "Project with id $projectId could not be found"

            verify(exactly = 0) {
                projectRepositoryMock.deleteById(projectId)
            }
        }
    }
})
