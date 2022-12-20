package de.partspicker.web.project.business

import de.partspicker.web.project.business.exceptions.GroupNotFoundException
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.business.objects.Project
import de.partspicker.web.project.persistance.GroupRepository
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.project.persistance.entities.ProjectEntity
import de.partspicker.web.test.generators.ProjectEntityGenerators
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.Optional

class ProjectServiceUnitTest : ShouldSpec({
    val projectRepositoryMock = mockk<ProjectRepository>()
    val groupRespositoryMock = mockk<GroupRepository>()
    val cut = ProjectService(
        projectRepository = projectRepositoryMock,
        groupRepository = groupRespositoryMock
    )

    context("create") {
        should("create new project & return it") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { groupRespositoryMock.existsById(projectEntity.group?.id!!) } returns true
            every { projectRepositoryMock.save(projectEntity) } returns projectEntity

            // when
            val returnedProject = cut.create(Project.from(projectEntity))

            verify(exactly = 1) {
                projectRepositoryMock.save(any())
            }

            returnedProject shouldBe Project.from(projectEntity)
        }

        should("throw GroupNotFoundException when given non-existent group") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { groupRespositoryMock.existsById(projectEntity.group?.id!!) } returns false

            // when
            val exception = shouldThrow<GroupNotFoundException> {
                cut.create(Project.from(projectEntity))
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

    context("update") {
        should("update the project with the given id with no group & return it") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next().copy(group = null)
            every { projectRepositoryMock.existsById(projectEntity.id) } returns true
            every { projectRepositoryMock.save(projectEntity) } returns projectEntity

            val project = Project.from(projectEntity)

            // when
            val updatedProject = cut.update(project)

            // then

            verify(exactly = 1) {
                projectRepositoryMock.existsById(projectEntity.id)
                projectRepositoryMock.save(projectEntity)
            }
            updatedProject shouldBe project
        }

        should("update the project with the given id & return it") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { projectRepositoryMock.existsById(projectEntity.id) } returns true
            every { projectRepositoryMock.save(projectEntity) } returns projectEntity
            every { groupRespositoryMock.existsById(projectEntity.group!!.id) } returns true

            val project = Project.from(projectEntity)

            // when
            val updatedProject = cut.update(project)

            // then

            verify(exactly = 1) {
                projectRepositoryMock.save(projectEntity)
                projectRepositoryMock.existsById(projectEntity.id)
                groupRespositoryMock.existsById(projectEntity.group!!.id)
            }
            updatedProject shouldBe project
        }

        should("throw ProjectNotFoundException when given non-existent id") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { projectRepositoryMock.existsById(projectEntity.id) } returns false

            val project = Project.from(projectEntity)

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.update(project)
            }

            // then
            verify(exactly = 1) {
                projectRepositoryMock.existsById(projectEntity.id)
            }

            verify(exactly = 0) {
                projectRepositoryMock.save(projectEntity)
            }

            exception.message shouldBe "Project with id ${project.id} could not be found"
        }

        should("throw GroupNotFoundException when given non-existent group") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { projectRepositoryMock.existsById(projectEntity.id) } returns true
            every { groupRespositoryMock.existsById(projectEntity.group!!.id) } returns false

            val project = Project.from(projectEntity)

            // when
            val exception = shouldThrow<GroupNotFoundException> {
                cut.update(project)
            }

            // then
            verify(exactly = 1) {
                projectRepositoryMock.existsById(projectEntity.id)
                groupRespositoryMock.existsById(projectEntity.group!!.id)
            }

            verify(exactly = 0) {
                projectRepositoryMock.save(projectEntity)
            }

            exception.message shouldBe "Group with id ${project.group!!.id} could not be found"
        }
    }
})
