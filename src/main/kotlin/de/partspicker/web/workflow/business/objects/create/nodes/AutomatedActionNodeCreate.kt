package de.partspicker.web.workflow.business.objects.create.nodes

import de.partspicker.web.common.util.elseThrow
import de.partspicker.web.workflow.business.exceptions.WorkflowIllegalStateException

class AutomatedActionNodeCreate(
    name: String,
    displayName: String,
    val automatedActionName: String
) : NodeCreate(name = name, displayName = displayName) {
    companion object {
        const val DISPLAY_NAME_IS_BLANK = "Display name must not be blank"
        const val AUTOMATED_ACTION_NAME_IS_BLANK = "Automated action name must not be blank"
    }

    init {
        displayName.isNotBlank() elseThrow WorkflowIllegalStateException(DISPLAY_NAME_IS_BLANK)
        automatedActionName.isNotBlank() elseThrow WorkflowIllegalStateException(AUTOMATED_ACTION_NAME_IS_BLANK)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as AutomatedActionNodeCreate

        if (displayName != other.displayName) return false
        if (automatedActionName != other.automatedActionName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + automatedActionName.hashCode()
        return result
    }

    override fun toString(): String {
        return "AutomatedActionNodeCreate(displayName='$displayName', automatedActionName='$automatedActionName')"
    }
}
