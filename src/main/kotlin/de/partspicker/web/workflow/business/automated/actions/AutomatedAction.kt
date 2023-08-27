package de.partspicker.web.workflow.business.automated.actions

import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.business.objects.InstanceValue
import de.partspicker.web.workflow.business.objects.enums.DisplayType
import org.springframework.stereotype.Component

@Component
fun interface AutomatedAction {
    /**
     * Executes an automated action & returns the name of the edge that should be used to advance the state.
     */
    fun execute(
        instance: Instance,
        instanceValues: List<InstanceValue>
    ): AutomatedActionResult
}

data class AutomatedActionResult(
    val chosenEdgeName: String,
    val message: String? = null,
    val displayType: DisplayType = DisplayType.DEFAULT,
    val modifiedInstanceValues: List<InstanceValue> = emptyList()
)
