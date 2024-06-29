package de.partspicker.web.inventory.persistence

import de.partspicker.web.inventory.persistence.results.AvailableItemTypeResult
import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import jakarta.persistence.EntityManager
import org.hibernate.search.mapper.orm.Search
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class AvailableItemTypeSearchRepository(
    private val entityManager: EntityManager,
    private val requiredItemTypeRepository: RequiredItemTypeRepository,
) {
    companion object {
        const val MAX_HITS = 10
        const val ITEM_TYPE_NAME_FIELD_NAME = "name"
    }

    fun searchByNameFilterRequired(name: String, projectId: Long): List<AvailableItemTypeResult> {
        val searchSession = Search.session(entityManager)

        val requiredItemTypeIdsForProject = this.requiredItemTypeRepository
            .findAllByProjectId(projectId = projectId, Pageable.unpaged())
            .map { itemType -> itemType.id.itemTypeId }
            .toList()

        return searchSession.search(ItemTypeEntity::class.java)
            .select { projectionFactory ->
                projectionFactory
                    .composite()
                    .from(
                        projectionFactory.id(Long::class.javaObjectType),
                        projectionFactory.field(ITEM_TYPE_NAME_FIELD_NAME, String::class.java)
                    )
                    .`as`(::AvailableItemTypeResult)
            }
            .where { predicateFactory ->
                predicateFactory
                    .bool()
                    .with {
                        if (requiredItemTypeIdsForProject.isNotEmpty()) {
                            it.mustNot(predicateFactory.id().matchingAny(requiredItemTypeIdsForProject))
                        }

                        it.should(
                            predicateFactory.match().field(ITEM_TYPE_NAME_FIELD_NAME).matching(name).fuzzy(1)
                        )
                        it.should(
                            predicateFactory.wildcard().field(ITEM_TYPE_NAME_FIELD_NAME).matching("*$name*")

                        )
                    }
            }
            .fetchHits(MAX_HITS) as List<AvailableItemTypeResult>
    }
}
