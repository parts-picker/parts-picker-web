package de.partspicker.web.workflow.business.rules

import de.partspicker.web.common.business.rules.Rule
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.workflow.business.exceptions.InstanceInactiveException
import de.partspicker.web.workflow.business.objects.Instance

class InstanceActiveRule(
    private val instance: Instance?
) : Rule {
    override fun valid() {
        require(instance != null) { "Cannot be null - this is a bug & will be resolved in the future" }

        (instance.active) elseThrow InstanceInactiveException(instanceId = instance.id)
    }
}
