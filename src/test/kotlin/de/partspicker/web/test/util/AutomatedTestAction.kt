package de.partspicker.web.test.util

import de.partspicker.web.workflow.business.automated.actions.AutomatedAction
import de.partspicker.web.workflow.business.automated.actions.AutomatedActionResult
import de.partspicker.web.workflow.business.objects.Instance
import de.partspicker.web.workflow.business.objects.InstanceValue
import org.springframework.stereotype.Component

/**
 * Dummy automated action implementation for passing the action name availability check.
 */
@Component(AutomatedTestAction.NAME)
class AutomatedTestAction : AutomatedAction {
    companion object {
        const val NAME = "test-action"
    }

    override fun execute(instance: Instance, instanceValues: List<InstanceValue>): AutomatedActionResult {
        return AutomatedActionResult("edge-name")
    }
}
