package de.parts_picker.web.common.exceptions

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import org.springframework.http.HttpStatus
import java.time.ZonedDateTime

@JsonPropertyOrder(value = ["status", "statusCode", "message", "errorCode", "path", "timestamp"])
data class ErrorInfo(
    val status: HttpStatus,
    val message: String = "",
    val errorCode: ErrorCode,
    val path: String,
    val timestamp: ZonedDateTime,
) {
    val statusCode: Int = status.value()
}
