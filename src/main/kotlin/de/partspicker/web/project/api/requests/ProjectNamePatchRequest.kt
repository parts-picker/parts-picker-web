package de.partspicker.web.project.api.requests

import jakarta.validation.constraints.NotBlank

class ProjectNamePatchRequest(
    @field:NotBlank(message = NAME_CANNOT_BE_BLANK)
    val name: String
) : ProjectPatchRequest {
    companion object {
        const val NAME_CANNOT_BE_BLANK = "Name cannot be blank"

        val DUMMY = ProjectNamePatchRequest(
            ""
        )
    }
}
