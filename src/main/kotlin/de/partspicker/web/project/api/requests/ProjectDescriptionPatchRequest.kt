package de.partspicker.web.project.api.requests

import jakarta.validation.constraints.Size

data class ProjectDescriptionPatchRequest(
    @field:Size(max = MAX_DESCRIPTION_SIZE, message = MAX_DESCRIPTION_SIZE_MESSAGE) val description: String?
) : ProjectPatchRequest {
    companion object {
        const val MAX_DESCRIPTION_SIZE = 10000
        const val MAX_DESCRIPTION_SIZE_MESSAGE = "Description size cannot be greater than $MAX_DESCRIPTION_SIZE"
    }
}
