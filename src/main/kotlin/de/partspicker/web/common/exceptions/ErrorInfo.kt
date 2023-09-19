package de.partspicker.web.common.exceptions

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import org.springframework.http.HttpStatus
import java.time.ZonedDateTime

@JsonPropertyOrder(value = ["status", "statusCode", "message", "errorCode", "errors", "path", "timestamp"])
data class ErrorInfo(
    val status: HttpStatus,
    val message: String = "",
    @Deprecated("Value errorCode will be removed in the future")
    val errorCode: ErrorCode? = null,
    val errors: List<ErrorDetail>,
    val path: String,
    val timestamp: ZonedDateTime
) {
    val statusCode: Int = status.value()
}
