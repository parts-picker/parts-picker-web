package de.partspicker.web.common.business

import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import de.partspicker.web.project.persistance.entities.GroupEntity
import de.partspicker.web.project.persistance.entities.ProjectEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.hibernate.search.mapper.orm.Search
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class StartupIndexingService(private val entityManager: EntityManager) {

    @EventListener
    @Transactional
    @Suppress("UnusedPrivateMember")
    fun onApplicationEvent(event: ContextRefreshedEvent?) {
        val searchSession = Search.session(entityManager)

        searchSession.massIndexer(GroupEntity::class.java).startAndWait()
        searchSession.massIndexer(ProjectEntity::class.java).startAndWait()
        searchSession.massIndexer(ItemTypeEntity::class.java).startAndWait()
    }
}
