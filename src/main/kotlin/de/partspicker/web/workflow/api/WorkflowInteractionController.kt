package de.partspicker.web.workflow.api

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
import de.partspicker.web.workflow.api.resources.NodeInfoResource
import de.partspicker.web.workflow.api.resources.NodeInfoResourceAssembler
import de.partspicker.web.workflow.business.WorkflowInteractionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkflowInteractionController(
    private val workflowInteractionService: WorkflowInteractionService,
    private val nodeInfoResourceAssembler: NodeInfoResourceAssembler
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @GetMapping("/instance/{instanceId}/node")
    fun handleGetCurrentNodeInfoForInstance(@PathVariable instanceId: Long): ResponseEntity<NodeInfoResource> {
        logger.info("=> GET request for the current node of the instance with id $instanceId")

        val nodeInfo = this.workflowInteractionService.readCurrentNodeByInstanceId(instanceId)
            ?: return ResponseEntity(HttpStatus.NO_CONTENT)

        return ResponseEntity(nodeInfoResourceAssembler.toModel(nodeInfo), HttpStatus.OK)
    }
}
