package de.partspicker.web.workflow.api

import de.partspicker.web.common.business.objects.enums.AccessLevel
import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.project.business.ProjectService
import de.partspicker.web.workflow.api.requests.AdvanceInstanceStateRequest
import de.partspicker.web.workflow.api.resources.InstanceInfoResource
import de.partspicker.web.workflow.api.resources.InstanceInfoResourceAssembler
import de.partspicker.web.workflow.business.WorkflowInteractionService
import de.partspicker.web.workflow.business.objects.InstanceValue
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkflowInteractionController(
    private val workflowInteractionService: WorkflowInteractionService,
    private val projectService: ProjectService,
    private val instanceInfoResourceAssembler: InstanceInfoResourceAssembler
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @PostMapping("/projects/{projectId}/instance/edges/{edgeId}")
    fun handleAdvanceInstanceState(
        @PathVariable projectId: Long,
        @PathVariable edgeId: Long,
        @RequestBody requestBody: AdvanceInstanceStateRequest?
    ): ResponseEntity<InstanceInfoResource> {
        logger.info(
            "=> POST request to advance the current node " +
                "for the project with id $projectId through edge with id $edgeId"
        )

        val instanceId = this.projectService.getInstanceIdOf(projectId, AccessLevel.EDIT)
        val updatedInstanceInfo = this.workflowInteractionService.advanceInstanceNodeByUser(
            instanceId,
            edgeId,
            requestBody?.let { InstanceValue.AsList.fromWithAutoTypeDetection(it.values) }
        )

        return ResponseEntity(instanceInfoResourceAssembler.toModel(updatedInstanceInfo, projectId), HttpStatus.OK)
    }

    @GetMapping("/projects/{projectId}/instance/node")
    fun handleGetInstanceInfo(@PathVariable projectId: Long): ResponseEntity<InstanceInfoResource> {
        logger.info("=> GET request for the current node of the project with id $projectId")

        val instanceId = this.projectService.getInstanceIdOf(projectId, AccessLevel.READ)
        val instanceInfo = this.workflowInteractionService.readInstanceInfo(instanceId)

        return ResponseEntity(instanceInfoResourceAssembler.toModel(instanceInfo, projectId), HttpStatus.OK)
    }
}
