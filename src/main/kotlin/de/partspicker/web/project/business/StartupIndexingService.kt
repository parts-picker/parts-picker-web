package de.partspicker.web.project.business

import de.partspicker.web.project.persistance.entities.GroupEntity
import de.partspicker.web.project.persistance.entities.ProjectEntity
import org.hibernate.SessionFactory
import org.hibernate.search.mapper.orm.Search
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class StartupIndexingService(private val sessionFactory: SessionFactory) {

    @PostConstruct
    fun index() {
        val searchSession = Search.session(sessionFactory.openSession())

        searchSession.massIndexer(GroupEntity::class.java).startAndWait()
        searchSession.massIndexer(ProjectEntity::class.java).startAndWait()
    }
}
