package de.partspicker.web.project.api.requests

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
sealed interface ProjectPatchRequest
