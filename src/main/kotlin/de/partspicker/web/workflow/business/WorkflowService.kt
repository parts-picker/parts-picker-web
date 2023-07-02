package de.partspicker.web.workflow.business

import de.partspicker.web.workflow.business.exceptions.WorkflowAlreadyExistsException
import de.partspicker.web.workflow.business.exceptions.WorkflowLatestVersionIsGreaterException
import de.partspicker.web.workflow.business.objects.create.WorkflowCreate
import de.partspicker.web.workflow.persistance.EdgeRepository
import de.partspicker.web.workflow.persistance.NodeRepository
import de.partspicker.web.workflow.persistance.WorkflowRepository
import de.partspicker.web.workflow.persistance.entities.EdgeEntity
import de.partspicker.web.workflow.persistance.entities.WorkflowEntity
import de.partspicker.web.workflow.persistance.entities.nodes.NodeEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WorkflowService(
    private val workflowRepository: WorkflowRepository,
    private val nodeRepository: NodeRepository,
    private val edgeRepository: EdgeRepository
) {
    fun exists(name: String, version: Long) = this.workflowRepository.existsByNameAndVersion(name, version)

    fun latestVersion(name: String) = this.workflowRepository.findLatestVersion(name)

    @Transactional(rollbackFor = [Exception::class])
    fun create(workflow: WorkflowCreate) {
        if (this.exists(workflow.name, workflow.version)) {
            throw WorkflowAlreadyExistsException(workflow.name, workflow.version)
        }

        val latestVersion = (this.latestVersion(workflow.name) ?: 0)
        if (workflow.version <= latestVersion) {
            throw WorkflowLatestVersionIsGreaterException(
                name = workflow.name,
                latestVersion = latestVersion,
                requestedVersion = workflow.version
            )
        }

        // convert & save workflow entity
        val newWorkflowEntity = WorkflowEntity.from(workflow)
        val savedWorkflowEntity = this.workflowRepository.save(newWorkflowEntity)

        // convert & save node entities & node lookup map
        val newNodeEntities = NodeEntity.AsList.from(workflow.nodes, savedWorkflowEntity.id)
        val savedNodeEntities = this.nodeRepository.saveAll(newNodeEntities)
        val nodeLookupMap = savedNodeEntities.associateBy { it.name }

        // convert & save edge entities
        val newEdgeEntities = EdgeEntity.AsList.from(workflow.edges, savedWorkflowEntity.id, nodeLookupMap)
        this.edgeRepository.saveAll(newEdgeEntities)
    }
}
