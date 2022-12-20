package de.partspicker.web.project.business

import de.partspicker.web.project.business.exceptions.GroupNotFoundException
import de.partspicker.web.project.persistance.GroupRepository
import de.partspicker.web.project.persistance.entities.GroupEntity
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val projectService: ProjectService
) {

    fun existsById(id: Long) = this.groupRepository.existsById(id)

    fun findById(id: Long): GroupEntity {
        val group = this.groupRepository.findById(id)

        if (group.isEmpty) {
            throw GroupNotFoundException(id)
        }

        return group.get()
    }

    fun findAll(): List<GroupEntity> = this.groupRepository.findAll()

    fun save(groupEntity: GroupEntity) = this.groupRepository.save(groupEntity)

    fun update(groupEntity: GroupEntity): GroupEntity {
        if (!this.existsById(groupEntity.id)) {
            throw GroupNotFoundException(groupEntity.id)
        }

        return this.groupRepository.save(groupEntity)
    }

    fun deleteById(id: Long) {
        if (!this.existsById(id)) {
            throw GroupNotFoundException(id)
        }

        this.projectService.removeGroupForAllById(id)
        this.groupRepository.deleteById(id)
    }
}
