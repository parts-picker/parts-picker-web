package de.partspicker.web.project.business

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.orgunit.business.OrgUnitAccessService
import de.partspicker.web.orgunit.business.exceptions.CreatorOrOrgUnitAccessDeniedException
import de.partspicker.web.orgunit.business.exceptions.OrgUnitAccessDeniedException
import de.partspicker.web.orgunit.persistence.OrgUnitRepository
import de.partspicker.web.project.business.exceptions.GroupNotFoundException
import de.partspicker.web.project.business.objects.Group
import de.partspicker.web.project.persistance.GroupRepository
import de.partspicker.web.project.persistance.entities.GroupEntity
import de.partspicker.web.test.generators.GroupEntityGenerators
import de.partspicker.web.test.generators.UserEntityGenerators
import de.partspicker.web.test.util.TestConstants.CRUD_REPOSITORY_EXTENSIONS
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull

class GroupServiceUnitTest : ShouldSpec({

    val groupRepositoryMock = mockk<GroupRepository>()
    val projectServiceMock = mockk<ProjectService>()
    val orgUnitRepositoryMock = mockk<OrgUnitRepository>()
    val orgUnitAccessServiceMock = mockk<OrgUnitAccessService>()
    val cut = GroupService(
        groupRepository = groupRepositoryMock,
        projectService = projectServiceMock,
        orgUnitRepository = orgUnitRepositoryMock,
        orgUnitAccessService = orgUnitAccessServiceMock
    )

    val currentUser = UserEntityGenerators.humanGenerator.next()

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
        clearMocks(groupRepositoryMock, projectServiceMock, orgUnitRepositoryMock, orgUnitAccessServiceMock)
    }

    context("getById") {
        should("return the group with the given id") {
            // given
            val groupEntity = GroupEntityGenerators.generator.next()
            every { groupRepositoryMock.findByIdOrNull(groupEntity.id) } returns groupEntity

            // when & then
            cut.getById(groupEntity.id) shouldBe Group.from(groupEntity)
        }

        should("throw GroupNotFoundException when no group with the given id exists") {
            // given
            val nonExistentId = Arb.long(min = 1).next()
            every { groupRepositoryMock.findByIdOrNull(nonExistentId) } returns null

            // when
            val exception = shouldThrow<GroupNotFoundException> { cut.getById(nonExistentId) }

            // then
            exception.message shouldBe "Group with id $nonExistentId could not be found"
        }

        should("require read access to the org unit of the group") {
            // given
            val groupEntity = GroupEntityGenerators.generator.next()
            every { groupRepositoryMock.findByIdOrNull(groupEntity.id) } returns groupEntity
            every {
                orgUnitAccessServiceMock.requireAtLeast(groupEntity.orgUnit.id, AccessLevel.READ)
            } throws OrgUnitAccessDeniedException(groupEntity.orgUnit.id, AccessLevel.READ)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.getById(groupEntity.id) }
        }
    }

    context("findAllForOrgUnit") {
        should("return every group of the given org unit") {
            // given
            val orgUnitId = 1L
            val groupEntity = GroupEntityGenerators.generator.next()
            every {
                groupRepositoryMock.findAllByOrgUnitId(orgUnitId, Pageable.unpaged())
            } returns PageImpl(listOf(groupEntity))

            // when & then
            cut.findAllForOrgUnit(orgUnitId, Pageable.unpaged()).content shouldContainExactly
                listOf(Group.from(groupEntity))
        }

        should("return an empty list when the given org unit holds no group") {
            // given
            val orgUnitId = 1L
            every {
                groupRepositoryMock.findAllByOrgUnitId(orgUnitId, Pageable.unpaged())
            } returns Page.empty()

            // when & then
            cut.findAllForOrgUnit(orgUnitId, Pageable.unpaged()).content shouldBe emptyList()
        }

        should("refuse & not read when the caller holds nothing in the given org unit") {
            // given
            val orgUnitId = 1L
            every {
                orgUnitAccessServiceMock.requireAtLeast(orgUnitId, AccessLevel.READ)
            } throws OrgUnitAccessDeniedException(orgUnitId, AccessLevel.READ)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.findAllForOrgUnit(orgUnitId, Pageable.unpaged()) }

            verify(exactly = 0) { groupRepositoryMock.findAllByOrgUnitId(any(), any()) }
        }
    }

    context("create") {
        should("store the group within the given org unit & return it") {
            // given
            val orgUnitId = 1L
            val groupEntity = GroupEntityGenerators.generator.next()
            every { orgUnitRepositoryMock.getReferenceById(orgUnitId) } returns groupEntity.orgUnit
            every { groupRepositoryMock.save(any()) } returns groupEntity

            // when
            val returnedGroup = cut.create(orgUnitId, name = "a group", description = "a description")

            // then
            returnedGroup shouldBe Group.from(groupEntity)
        }

        should("name the current user as the creator of the stored group") {
            // given
            val orgUnitId = 1L
            val groupEntity = GroupEntityGenerators.generator.next()
            val savedGroup = slot<GroupEntity>()
            every { orgUnitRepositoryMock.getReferenceById(orgUnitId) } returns groupEntity.orgUnit
            every { groupRepositoryMock.save(capture(savedGroup)) } returns groupEntity

            // when
            cut.create(orgUnitId, name = "a group", description = null)

            // then
            savedGroup.captured.name shouldBe "a group"
            savedGroup.captured.description shouldBe null
            savedGroup.captured.creation.createdBy shouldBe currentUser
        }

        should("refuse & not store when the caller may not edit the given org unit") {
            // given
            val orgUnitId = 1L
            every {
                orgUnitAccessServiceMock.requireAtLeast(orgUnitId, AccessLevel.EDIT)
            } throws OrgUnitAccessDeniedException(orgUnitId, AccessLevel.EDIT)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.create(orgUnitId, "a group", null) }

            verify(exactly = 0) { groupRepositoryMock.save(any()) }
        }
    }

    context("update") {
        should("update name & description of the group with the given id") {
            // given
            val groupEntity = GroupEntityGenerators.generator.next()
            every { groupRepositoryMock.findByIdOrNull(groupEntity.id) } returns groupEntity
            every { groupRepositoryMock.save(groupEntity) } returns groupEntity

            // when
            val updatedGroup = cut.update(groupEntity.id, name = "new name", description = "new description")

            // then
            updatedGroup.name shouldBe "new name"
            updatedGroup.description shouldBe "new description"
        }

        should("keep the org unit & the creation info of the updated group") {
            // given
            val groupEntity = GroupEntityGenerators.generator.next()
            val originalOrgUnit = groupEntity.orgUnit
            val originalCreation = groupEntity.creation
            every { groupRepositoryMock.findByIdOrNull(groupEntity.id) } returns groupEntity
            every { groupRepositoryMock.save(groupEntity) } returns groupEntity

            // when
            cut.update(groupEntity.id, name = "new name", description = null)

            // then
            groupEntity.orgUnit shouldBe originalOrgUnit
            groupEntity.creation shouldBe originalCreation
        }

        should("throw GroupNotFoundException when no group with the given id exists") {
            // given
            val nonExistentId = Arb.long(min = 1).next()
            every { groupRepositoryMock.findByIdOrNull(nonExistentId) } returns null

            // when & then
            shouldThrow<GroupNotFoundException> { cut.update(nonExistentId, "new name", null) }
        }

        should("refuse & not store when the caller may not edit the org unit of the group") {
            // given
            val groupEntity = GroupEntityGenerators.generator.next()
            every { groupRepositoryMock.findByIdOrNull(groupEntity.id) } returns groupEntity
            every {
                orgUnitAccessServiceMock.requireAtLeast(groupEntity.orgUnit.id, AccessLevel.EDIT)
            } throws OrgUnitAccessDeniedException(groupEntity.orgUnit.id, AccessLevel.EDIT)

            // when & then
            shouldThrow<OrgUnitAccessDeniedException> { cut.update(groupEntity.id, "new name", null) }

            verify(exactly = 0) { groupRepositoryMock.save(any()) }
        }
    }

    context("delete") {
        should("detach every project of the group & then delete it") {
            // given
            val groupEntity = GroupEntityGenerators.generator.next()
            every { groupRepositoryMock.findByIdOrNull(groupEntity.id) } returns groupEntity
            every { projectServiceMock.removeGroupForAllById(groupEntity.id) } just runs
            every { groupRepositoryMock.delete(groupEntity) } returns Unit

            // when
            cut.delete(groupEntity.id)

            // then
            verify(exactly = 1) {
                projectServiceMock.removeGroupForAllById(groupEntity.id)
                groupRepositoryMock.delete(groupEntity)
            }
        }

        should("throw GroupNotFoundException when no group with the given id exists") {
            // given
            val nonExistentId = Arb.long(min = 1).next()
            every { groupRepositoryMock.findByIdOrNull(nonExistentId) } returns null

            // when & then
            shouldThrow<GroupNotFoundException> { cut.delete(nonExistentId) }
        }

        should("refuse & not delete when the caller neither created the group nor maintains its org unit") {
            // given
            val groupEntity = GroupEntityGenerators.generator.next()
            every { groupRepositoryMock.findByIdOrNull(groupEntity.id) } returns groupEntity
            every {
                orgUnitAccessServiceMock.requireMemberCreatorOrAtLeast(any(), any(), AccessLevel.MAINTAIN)
            } throws CreatorOrOrgUnitAccessDeniedException(groupEntity.orgUnit.id, AccessLevel.MAINTAIN)

            // when & then
            shouldThrow<CreatorOrOrgUnitAccessDeniedException> { cut.delete(groupEntity.id) }

            verify(exactly = 0) {
                projectServiceMock.removeGroupForAllById(any())
                groupRepositoryMock.delete(any())
            }
        }
    }
})
