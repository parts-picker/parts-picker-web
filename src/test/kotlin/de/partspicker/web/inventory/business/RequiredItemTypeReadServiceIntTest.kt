package de.partspicker.web.inventory.business

import de.partspicker.web.inventory.business.objects.CreateOrUpdateRequiredItemType
import de.partspicker.web.inventory.business.objects.RequiredItemType
import de.partspicker.web.item.business.ItemTypeService
import de.partspicker.web.item.business.objects.ItemType
import de.partspicker.web.project.business.ProjectService
import de.partspicker.web.project.business.objects.CreateProject
import de.partspicker.web.project.business.objects.Project
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("integration")
@Transactional
class RequiredItemTypeReadServiceIntTest(
    private val cut: RequiredItemTypeReadService,
    // helpers
    private val projectService: ProjectService,
    private val itemTypeService: ItemTypeService,
    private val requiredItemTypeService: RequiredItemTypeService,
    private val entityManager: EntityManager

) : ShouldSpec({
    // helpers
    fun flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

    fun setupProject(): Project {
        val project = projectService.create(
            CreateProject(
                "Test Project",
                ""
            )
        )
        flushAndClear()

        return project
    }

    fun setupItemType(name: String = "itemType"): ItemType {
        return itemTypeService.create(
            ItemType(id = 0, name = name, description = "")
        )
    }

    fun setupRequiredItemType(projectId: Long, itemTypeId: Long, requiredAmount: Long = 1): RequiredItemType {
        return requiredItemTypeService.createOrUpdate(
            CreateOrUpdateRequiredItemType(
                projectId = projectId,
                itemTypeId = itemTypeId,
                requiredAmount = requiredAmount
            )
        )
    }

    context("readByProjectIdAndItemTypeId") {
        should("return the correct required item type") {
            // given
            val project = setupProject()
            val itemType = setupItemType()
            val requiredAmount = 42L
            setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id, requiredAmount)

            // when
            val requiredItemType = cut.readByProjectIdAndItemTypeId(projectId = project.id, itemTypeId = itemType.id)

            // then
            requiredItemType.projectId shouldBe project.id
            requiredItemType.itemType.id shouldBe itemType.id
            requiredItemType.requiredAmount shouldBe requiredAmount
            requiredItemType.assignedAmount shouldBe 0
        }
    }

    context("readAllByProjectId") {
        should("return all required item types") {
            // given
            val project = setupProject()

            val itemTypeAmount = 7
            val requiredAmount = 2L

            repeat(itemTypeAmount) {
                val itemType = setupItemType()
                setupRequiredItemType(projectId = project.id, itemTypeId = itemType.id, requiredAmount)
            }

            // when
            val requiredItemTypes = cut.readAllByProjectId(project.id, Pageable.unpaged())

            // then
            requiredItemTypes shouldHaveSize itemTypeAmount
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
