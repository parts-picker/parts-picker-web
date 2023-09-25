package de.partspicker.web.project.api.requests

import jakarta.validation.constraints.NotBlank

data class ProjectCopyRequest(
    @field:NotBlank(message = NAME_CANNOT_BE_BLANK)
    val name: String
) {
    companion object {
        const val NAME_CANNOT_BE_BLANK = "Name cannot be blank"

        val DUMMY = ProjectCopyRequest("")
    }
}
