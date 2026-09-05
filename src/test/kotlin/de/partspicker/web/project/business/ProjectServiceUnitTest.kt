package de.partspicker.web.project.business

import de.partspicker.web.common.business.exceptions.OrRuleException
import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.inventory.business.RequiredItemTypeService
import de.partspicker.web.item.persistance.ItemRepository
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.orgunit.business.exceptions.OrgUnitAccessDeniedException
import de.partspicker.web.orgunit.persistence.OrgUnitRepository
import de.partspicker.web.project.business.exceptions.GroupNotFoundException
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.project.business.objects.CreateProject
import de.partspicker.web.project.business.objects.Project
import de.partspicker.web.project.persistance.GroupRepository
import de.partspicker.web.project.persistance.ProjectRepository
import de.partspicker.web.project.persistance.entities.ProjectEntity
import de.partspicker.web.test.generators.ProjectEntityGenerators
import de.partspicker.web.test.generators.ProjectGenerators
import de.partspicker.web.test.generators.UserEntityGenerators
import de.partspicker.web.test.generators.id
import de.partspicker.web.test.generators.workflow.InstanceEntityGenerators
import de.partspicker.web.test.generators.workflow.WorkflowEntityGenerators
import de.partspicker.web.test.util.TestConstants.CRUD_REPOSITORY_EXTENSIONS
import de.partspicker.web.workflow.business.WorkflowInteractionService
import de.partspicker.web.workflow.business.exceptions.InstanceInactiveException
import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.persistence.InstanceRepository
import de.partspicker.web.workflow.persistence.entities.nodes.UserActionNodeEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import io.mockk.unmockkStatic
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull

class ProjectServiceUnitTest : ShouldSpec({
    val projectRepositoryMock = mockk<ProjectRepository>()
    val groupRepositoryMock = mockk<GroupRepository>()
    val workflowInteractionServiceMock = mockk<WorkflowInteractionService>()
    val itemRepositoryMock = mockk<ItemRepository>()
    val requiredItemTypeServiceMock = mockk<RequiredItemTypeService>()
    val instanceRepositoryMock = mockk<InstanceRepository>()
    val orgUnitRepositoryMock = mockk<OrgUnitRepository>()
    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val currentUser = UserEntityGenerators.humanGenerator.next()
    val cut = ProjectService(
        projectRepository = projectRepositoryMock,
        groupRepository = groupRepositoryMock,
        workflowInteractionService = workflowInteractionServiceMock,
        itemRepository = itemRepositoryMock,
        requiredItemTypeService = requiredItemTypeServiceMock,
        instanceRepository = instanceRepositoryMock,
        orgUnitRepository = orgUnitRepositoryMock,
        orgUnitAccessService = orgUnitAccessServiceMock
    )

    beforeSpec {
        mockkStatic(CRUD_REPOSITORY_EXTENSIONS)
    }

    afterSpec {
        unmockkStatic(CRUD_REPOSITORY_EXTENSIONS)
    }

    beforeTest {
        every { orgUnitAccessServiceMock.requireAtLeast(any(), any()) } returns Unit
        every { orgUnitAccessServiceMock.requireMemberCreatorOrAtLeast(any(), any(), any()) } returns Unit
        every { orgUnitAccessServiceMock.currentUser() } returns currentUser
    }

    afterTest {
        clearMocks(projectRepositoryMock, orgUnitAccessServiceMock)
    }

    context("create") {
        should("refuse & not store when the caller may not edit the given org unit") {
            // given
            every {
                orgUnitAccessServiceMock.requireAtLeast(1L, AccessLevel.EDIT)
            } throws OrgUnitAccessDeniedException(1L, AccessLevel.EDIT)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> {
                cut.create(1L, CreateProject(name = "a name", shortDescription = null))
            }

            verify(exactly = 0) { projectRepositoryMock.save(any()) }
        }

        should("create new project & return it") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { groupRepositoryMock.findByIdOrNull(projectEntity.group?.id!!) } returns projectEntity.group!!
            every { projectRepositoryMock.save(any()) } returns projectEntity
            every { orgUnitRepositoryMock.getReferenceById(projectEntity.orgUnit.id) } returns projectEntity.orgUnit
            every { workflowInteractionServiceMock.startProjectWorkflow() } returns
                Instance.from(projectEntity.workflowInstance)
            every { instanceRepositoryMock.getReferenceById(any()) } returns mockk()

            // when
            val returnedProject = cut.create(
                projectEntity.orgUnit.id,
                CreateProject(
                    name = projectEntity.name,
                    shortDescription = projectEntity.shortDescription,
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
            every { groupRepositoryMock.findByIdOrNull(projectEntity.group?.id!!) } returns null

            // when
            val exception = shouldThrow<GroupNotFoundException> {
                cut.create(
                    projectEntity.orgUnit.id,
                    CreateProject(
                        name = projectEntity.name,
                        shortDescription = projectEntity.shortDescription,
                        groupId = projectEntity.group?.id
                    )
                )
            }

            // then
            exception.message shouldBe "Group with id ${projectEntity.group?.id!!} could not be found"
        }
    }

    context("copy") {
        should("create a new project based on the source project with the given id") {
            // given
            val sourceProjectEntity = ProjectEntityGenerators.generator.single()
            val sourceProject = Project.from(sourceProjectEntity)
            every { projectRepositoryMock.findByIdOrNull(sourceProject.id) } returns sourceProjectEntity

            val cutSpy = spyk(cut)
            val targetProject = ProjectGenerators.generator.single()
            every { cutSpy.create(any(), any()) } returns targetProject

            every {
                requiredItemTypeServiceMock.copyAllToTargetProjectByProjectId(sourceProject.id, targetProject.id)
            } just runs

            val copiedProjectName = "copied project"

            // when
            cutSpy.copy(sourceProject.id, copiedProjectName)

            // then
            verify {
                cutSpy.create(
                    eq(sourceProjectEntity.orgUnit.id),
                    withArg {
                        it.name shouldBe copiedProjectName
                        it.shortDescription shouldBe sourceProject.shortDescription
                        it.description shouldBe sourceProject.description
                        it.groupId shouldBe sourceProject.group?.id
                        it.sourceProjectId shouldBe sourceProject.id
                    }
                )
                requiredItemTypeServiceMock.copyAllToTargetProjectByProjectId(sourceProject.id, targetProject.id)
            }
        }
    }

    context("findAllForOrgUnit") {
        should("return all items") {
            // given
            val projectsPage: Page<ProjectEntity> = PageImpl(
                listOf(
                    ProjectEntityGenerators.generator.next(),
                    ProjectEntityGenerators.generator.next()
                )
            )
            every { projectRepositoryMock.findAllByOrgUnitId(1L, Pageable.unpaged()) } returns projectsPage

            // when
            val returnedProjects = cut.findAllForOrgUnit(1L, Pageable.unpaged())

            // then
            returnedProjects shouldBe Project.AsPage.from(projectsPage)
        }

        should("return empty list when no projects available") {
            // given
            every { projectRepositoryMock.findAllByOrgUnitId(1L, Pageable.unpaged()) } returns Page.empty()

            // when
            val returnedProjects = cut.findAllForOrgUnit(1L, Pageable.unpaged())

            // then
            returnedProjects shouldBe Page.empty()
        }
    }

    context("getById") {
        should("refuse when the caller holds nothing in the org unit of the project") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { projectRepositoryMock.findByIdOrNull(projectEntity.id) } returns projectEntity
            every {
                orgUnitAccessServiceMock.requireAtLeast(projectEntity.orgUnit.id, AccessLevel.READ)
            } throws OrgUnitAccessDeniedException(projectEntity.orgUnit.id, AccessLevel.READ)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.getById(projectEntity.id) }
        }

        should("return correct project when given existent id") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every { projectRepositoryMock.findByIdOrNull(projectEntity.id) } returns projectEntity

            // when
            val returnedProject = cut.getById(projectEntity.id)

            // then
            returnedProject shouldBe Project.from(projectEntity)
        }

        should("throw ProjectNotFoundException when given non-existent id") {
            // given
            val randomId = Arb.long(min = 1).next()
            every { projectRepositoryMock.findByIdOrNull(randomId) } returns null

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.getById(randomId)
            }

            // then
            exception.message shouldBe "Project with id $randomId could not be found"
        }
    }

    context("findByInstanceId") {
        should("return correct project when given existent id") {
            // given
            val projectEntity = ProjectEntityGenerators.generator.next()
            every {
                projectRepositoryMock.findByWorkflowInstanceId(projectEntity.workflowInstance.id)
            } returns projectEntity

            // when
            val returnedProject = cut.findByInstanceId(projectEntity.workflowInstance.id)

            // then
            returnedProject shouldBe Project.from(projectEntity)
        }

        should("return null when given non-existent id") {
            // given
            val randomId = Arb.id().single()
            every { projectRepositoryMock.findByWorkflowInstanceId(randomId) } returns null

            // when
            val returnedProject = cut.findByInstanceId(randomId)

            // then
            returnedProject shouldBe null
        }
    }

    context("update") {
        should("update the project with the given id with no group & return it") {
            // given
            val activeInstanceEntity = InstanceEntityGenerators.generator.single().copy(active = true)
            val projectEntity = ProjectEntityGenerators.generator.single().copy(
                group = null,
                workflowInstance = activeInstanceEntity
            )
            every { projectRepositoryMock.findByIdOrNull(projectEntity.id) } returns projectEntity
            every { projectRepositoryMock.save(projectEntity) } returns projectEntity

            // when
            val updatedProject = cut.update(
                projectId = projectEntity.id,
                shortDescription = projectEntity.shortDescription,
                groupId = null
            )

            // then
            verify(exactly = 1) {
                projectRepositoryMock.save(projectEntity)
            }

            updatedProject.name shouldBe projectEntity.name
            updatedProject.shortDescription shouldBe projectEntity.shortDescription
        }

        should("update the project with the given id & return it") {
            // given
            val activeInstanceEntity = InstanceEntityGenerators.generator.single().copy(active = true)
            val projectEntity = ProjectEntityGenerators.generator.single().copy(workflowInstance = activeInstanceEntity)
            every { projectRepositoryMock.findByIdOrNull(projectEntity.id) } returns projectEntity
            every { projectRepositoryMock.save(any()) } returns projectEntity
            every { groupRepositoryMock.findByIdOrNull(projectEntity.group!!.id) } returns projectEntity.group!!

            // when
            val updatedProject = cut.update(
                projectId = projectEntity.id,
                shortDescription = projectEntity.shortDescription,
                groupId = projectEntity.group!!.id
            )

            // then
            verify(exactly = 1) {
                projectRepositoryMock.save(any())
                groupRepositoryMock.findByIdOrNull(projectEntity.group!!.id)
            }

            updatedProject.name shouldBe projectEntity.name
            updatedProject.shortDescription shouldBe projectEntity.shortDescription
            updatedProject.group!!.id shouldBe projectEntity.group!!.id
            updatedProject.workflowInstanceId shouldBe projectEntity.workflowInstance.id
        }

        should("throw ProjectNotFoundException when given non-existent id") {
            // given
            val nonExistentId = 666L
            every { projectRepositoryMock.findByIdOrNull(nonExistentId) } returns null

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.update(
                    projectId = nonExistentId,
                    shortDescription = "description",
                    groupId = null
                )
            }

            // then
            verify(exactly = 0) {
                projectRepositoryMock.save(any())
            }

            exception.message shouldBe "Project with id $nonExistentId could not be found"
        }

        should("throw InstanceInactiveException when given inactive instance") {
            // given
            val inactiveInstanceEntity = InstanceEntityGenerators.generator.single().copy(active = false)
            val projectEntity = ProjectEntityGenerators.generator.single().copy(
                workflowInstance = inactiveInstanceEntity
            )
            every { projectRepositoryMock.findByIdOrNull(projectEntity.id) } returns projectEntity

            // when
            val exception = shouldThrow<InstanceInactiveException> {
                cut.update(
                    projectId = projectEntity.id,
                    shortDescription = "description",
                    groupId = null
                )
            }

            // then
            verify(exactly = 0) {
                projectRepositoryMock.save(any())
            }

            exception.message shouldBe
                "The instance with the given id ${inactiveInstanceEntity.id} is inactive & cannot be modified"
        }

        should("throw GroupNotFoundException when given non-existent group") {
            // given
            val activeInstanceEntity = InstanceEntityGenerators.generator.single().copy(active = true)
            val projectEntity = ProjectEntityGenerators.generator.single().copy(workflowInstance = activeInstanceEntity)
            val nonExistentId = 666L
            every { projectRepositoryMock.findByIdOrNull(projectEntity.id) } returns projectEntity
            every { groupRepositoryMock.findByIdOrNull(nonExistentId) } returns null

            // when
            val exception = shouldThrow<GroupNotFoundException> {
                cut.update(
                    projectId = projectEntity.id,
                    shortDescription = "description",
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

    context("updateDescription") {
        should("update the description of the project with the given id & return it") {
            // given
            val inactiveInstanceEntity = InstanceEntityGenerators.generator.single().copy(active = true)
            val projectEntity = ProjectEntityGenerators.generator.single().copy(
                workflowInstance = inactiveInstanceEntity
            )
            every { projectRepositoryMock.findByIdOrNull(projectEntity.id) } returns projectEntity
            every { projectRepositoryMock.save(any()) } returns projectEntity

            val description = Arb.string(200..400).single()
            // when
            val updatedProject = cut.updateDescription(
                projectId = projectEntity.id,
                description = description
            )

            // then
            verify {
                projectRepositoryMock.save(any())
            }

            updatedProject.name shouldBe projectEntity.name
            updatedProject.shortDescription shouldBe projectEntity.shortDescription
            updatedProject.description shouldBe description
            updatedProject.group?.id shouldBe projectEntity.group?.id
            updatedProject.workflowInstanceId shouldBe projectEntity.workflowInstance.id
        }

        should("throw ProjectNotFoundException when given non-existent id") {
            // given
            val nonExistentId = 666L
            every { projectRepositoryMock.findByIdOrNull(nonExistentId) } returns null

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.updateDescription(
                    projectId = nonExistentId,
                    description = "description"
                )
            }

            // then
            verify(exactly = 0) {
                projectRepositoryMock.save(any())
            }

            exception.message shouldBe "Project with id $nonExistentId could not be found"
        }

        should("throw InstanceInactiveException when given inactive instance") {
            // given
            val inactiveInstanceEntity = InstanceEntityGenerators.generator.single().copy(active = false)
            val projectEntity = ProjectEntityGenerators.generator.single().copy(
                workflowInstance = inactiveInstanceEntity
            )
            every { projectRepositoryMock.findByIdOrNull(projectEntity.id) } returns projectEntity

            val description = Arb.string(200..400).single()

            // when
            val exception = shouldThrow<InstanceInactiveException> {
                cut.updateDescription(
                    projectId = projectEntity.id,
                    description = description
                )
            }

            // then
            verify(exactly = 0) {
                projectRepositoryMock.save(any())
            }

            exception.message shouldBe
                "The instance with the given id ${inactiveInstanceEntity.id} is inactive & cannot be modified"
        }
    }

    context("delete") {
        should("delete the correct project & refresh inventory when given project status is planning") {
            // given
            val nodeEntity = UserActionNodeEntity(
                id = 1L,
                workflow = WorkflowEntityGenerators.generator.single(),
                name = "planning",
                displayName = "Planning"
            )
            val projectEntity = ProjectEntityGenerators.generator.single().copy(
                workflowInstance = InstanceEntityGenerators.generator.single().copy(currentNode = nodeEntity)
            )

            every { projectRepositoryMock.findByIdOrNull(projectEntity.id) } returns projectEntity
            every { itemRepositoryMock.updateUnassignAllByAssignedProjectId(projectEntity.id) } just runs
            every { requiredItemTypeServiceMock.deleteAllByProjectId(projectEntity.id) } just runs
            every { projectRepositoryMock.delete(projectEntity) } just runs

            // when
            cut.delete(projectEntity.id)

            // then
            verify {
                projectRepositoryMock.delete(projectEntity)
                itemRepositoryMock.updateUnassignAllByAssignedProjectId(projectEntity.id)
                requiredItemTypeServiceMock.deleteAllByProjectId(projectEntity.id)
            }
        }

        should("delete the correct project & refresh inventory when given project status is implementation") {
            // given
            val nodeEntity = UserActionNodeEntity(
                id = 1L,
                workflow = WorkflowEntityGenerators.generator.single(),
                name = "implementation",
                displayName = "Implementation"
            )
            val projectEntity = ProjectEntityGenerators.generator.single().copy(
                workflowInstance = InstanceEntityGenerators.generator.single().copy(currentNode = nodeEntity)
            )

            every { projectRepositoryMock.findByIdOrNull(projectEntity.id) } returns projectEntity
            every { itemRepositoryMock.updateUnassignAllByAssignedProjectId(projectEntity.id) } just runs
            every { requiredItemTypeServiceMock.deleteAllByProjectId(projectEntity.id) } just runs
            every { projectRepositoryMock.delete(projectEntity) } just runs

            // when
            cut.delete(projectEntity.id)

            // then
            verify {
                projectRepositoryMock.delete(projectEntity)
                itemRepositoryMock.updateUnassignAllByAssignedProjectId(projectEntity.id)
                requiredItemTypeServiceMock.deleteAllByProjectId(projectEntity.id)
            }
        }

        should("throw OrRuleException when given project status not equal to planning or implementation") {
            // given
            val nodeEntity = UserActionNodeEntity(
                id = 1L,
                workflow = WorkflowEntityGenerators.generator.single(),
                name = "non-desired-name",
                displayName = "Something"
            )
            val projectEntity = ProjectEntityGenerators.generator.single().copy(
                workflowInstance = InstanceEntityGenerators.generator.single().copy(currentNode = nodeEntity)
            )

            every { projectRepositoryMock.findByIdOrNull(projectEntity.id) } returns projectEntity

            // when
            val exception = shouldThrow<OrRuleException> { cut.delete(projectEntity.id) }

            // then
            exception.message shouldBe OrRuleException.MESSAGE
            exception.exceptions shouldHaveSize 2

            verify(exactly = 0) {
                projectRepositoryMock.delete(projectEntity)
                itemRepositoryMock.updateUnassignAllByAssignedProjectId(projectEntity.id)
                requiredItemTypeServiceMock.deleteAllByProjectId(projectEntity.id)
            }
        }

        should("throw ProjectNotFoundException when given non-existent id") {
            // given
            val projectId = Arb.long(min = 1).next()

            every { projectRepositoryMock.findByIdOrNull(projectId) } returns null

            // when
            val exception = shouldThrow<ProjectNotFoundException> {
                cut.delete(projectId)
            }

            // then
            exception.message shouldBe "Project with id $projectId could not be found"

            verify(exactly = 0) {
                projectRepositoryMock.delete(any())
            }
        }
    }
})
