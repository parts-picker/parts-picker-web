package de.partspicker.web.workflow.api

import de.partspicker.web.common.util.LoggingUtil
import de.partspicker.web.common.util.logger
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
    private val instanceInfoResourceAssembler: InstanceInfoResourceAssembler
) {
    companion object : LoggingUtil {
        val logger = logger()
    }

    @PostMapping("/instance/{instanceId}/edges/{edgeId}")
    fun handleAdvanceInstanceState(
        @PathVariable instanceId: Long,
        @PathVariable edgeId: Long,
        @RequestBody requestBody: AdvanceInstanceStateRequest?
    ): ResponseEntity<InstanceInfoResource> {
        logger.info(
            "=> POST request to advance the current node " +
                "for instance with id $instanceId through edge with id $edgeId"
        )

        val updatedInstanceInfo = this.workflowInteractionService.advanceInstanceNodeByUser(
            instanceId,
            edgeId,
            requestBody?.let { InstanceValue.AsList.fromWithAutoTypeDetection(it.values) }
        )

        return ResponseEntity(instanceInfoResourceAssembler.toModel(updatedInstanceInfo), HttpStatus.OK)
    }

    @GetMapping("/instance/{instanceId}/node")
    fun handleGetInstanceInfo(@PathVariable instanceId: Long): ResponseEntity<InstanceInfoResource> {
        logger.info("=> GET request for the current node of the instance with id $instanceId")

        val instanceInfo = this.workflowInteractionService.readInstanceInfo(instanceId)
            ?: return ResponseEntity(HttpStatus.NO_CONTENT)

        return ResponseEntity(instanceInfoResourceAssembler.toModel(instanceInfo), HttpStatus.OK)
    }
}
