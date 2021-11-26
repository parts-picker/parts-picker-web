package de.parts_picker.web.common.exceptions

import de.parts_picker.web.item.business.exceptions.ItemNotFoundException
import de.parts_picker.web.project.business.exceptions.GroupNotFoundException
import de.parts_picker.web.project.business.exceptions.ProjectNotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.ZonedDateTime

@ControllerAdvice
class GlobalExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(value=[
        GroupNotFoundException::class,
        ProjectNotFoundException::class,
        ItemNotFoundException::class
    ])
    fun handleEntityNotFoundException(
        exc: Exception,
        webRequest : ServletWebRequest,
    ): ResponseEntity<Any> {
        val info = ErrorInfo(
            HttpStatus.NOT_FOUND,
            exc.localizedMessage,
            ErrorCode.EntityNotFound,
            webRequest.request.requestURI,
            ZonedDateTime.now()
        )

        return handleExceptionInternal(
            exc,
            info,
            HttpHeaders(),
            info.status,
            webRequest)
    }
}