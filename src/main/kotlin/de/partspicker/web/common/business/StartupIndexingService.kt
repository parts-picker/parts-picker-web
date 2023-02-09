package de.partspicker.web.common.business

import de.partspicker.web.item.persistance.entities.ItemTypeEntity
import de.partspicker.web.project.persistance.entities.GroupEntity
import de.partspicker.web.project.persistance.entities.ProjectEntity
import jakarta.annotation.PostConstruct
import org.hibernate.SessionFactory
import org.hibernate.search.mapper.orm.Search
import org.springframework.stereotype.Service

@Service
class StartupIndexingService(private val sessionFactory: SessionFactory) {

    @PostConstruct
    fun index() {
        val searchSession = Search.session(sessionFactory.openSession())

        searchSession.massIndexer(GroupEntity::class.java).startAndWait()
        searchSession.massIndexer(ProjectEntity::class.java).startAndWait()
        searchSession.massIndexer(ItemTypeEntity::class.java).startAndWait()
    }
}
