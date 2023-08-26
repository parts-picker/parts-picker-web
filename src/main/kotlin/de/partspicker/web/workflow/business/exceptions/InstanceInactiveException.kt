package de.partspicker.web.workflow.business.exceptions

import de.partspicker.web.common.business.exceptions.RuleException

class InstanceInactiveException(instanceId: Long) : RuleException(
    "The instance with the given id $instanceId is inactive & cannot be modified"
)
