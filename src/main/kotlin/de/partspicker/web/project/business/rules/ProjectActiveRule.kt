package de.partspicker.web.project.business.rules

import de.partspicker.web.common.business.rules.Rule
import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.project.business.objects.Project
import de.partspicker.web.workflow.business.exceptions.InstanceInactiveException

class ProjectActiveRule(private val project: Project) : Rule {
    override fun valid() {
        project.active elseThrow InstanceInactiveException(instanceId = project.workflowInstanceId)
    }
}
