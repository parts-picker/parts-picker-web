package de.partspicker.web.common.exceptions

import de.partspicker.web.item.business.exceptions.ItemNotFoundException
import de.partspicker.web.item.business.exceptions.ItemTypeNotFoundException
import de.partspicker.web.project.business.exceptions.GroupNotFoundException
import de.partspicker.web.project.business.exceptions.ProjectNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeSourceNotMatchingException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotActiveException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowNodeNameNotFoundException
import de.partspicker.web.workflow.business.exceptions.WorkflowStartedWithNonStartNodeException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.ZonedDateTime

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(
        value = [
            GroupNotFoundException::class,
            ProjectNotFoundException::class,
            ItemNotFoundException::class,
            ItemTypeNotFoundException::class,
            WorkflowInstanceNotFoundException::class,
            WorkflowEdgeNotFoundException::class,
            WorkflowNodeNameNotFoundException::class,
            WorkflowNameNotFoundException::class
        ]
    )
    fun handleEntityNotFoundException(
        exc: Exception,
        webRequest: ServletWebRequest
    ): ResponseEntity<Any>? {
        val info = ErrorInfo(
            HttpStatus.NOT_FOUND,
            exc.localizedMessage,
            ErrorCode.EntityNotFound,
            mapOf(Pair(exc.javaClass.simpleName, exc.localizedMessage)),
            webRequest.request.requestURI,
            ZonedDateTime.now()
        )

        return handleExceptionInternal(
            exc,
            info,
            HttpHeaders(),
            info.status,
            webRequest
        )
    }

    @ExceptionHandler(
        value = [
            WorkflowEdgeSourceNotMatchingException::class,
            WorkflowInstanceNotActiveException::class,
            WorkflowStartedWithNonStartNodeException::class
        ]
    )
    fun handleWorkflowBusinessException(
        exc: Exception,
        webRequest: ServletWebRequest
    ): ResponseEntity<Any>? {
        val info = ErrorInfo(
            HttpStatus.UNPROCESSABLE_ENTITY,
            exc.localizedMessage,
            null,
            mapOf(Pair(exc.javaClass.simpleName, exc.localizedMessage)),
            webRequest.request.requestURI,
            ZonedDateTime.now()
        )

        return handleExceptionInternal(
            exc,
            info,
            HttpHeaders(),
            info.status,
            webRequest
        )
    }

    override fun handleMethodArgumentNotValid(
        exc: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val info = ErrorInfo(
            status = HttpStatus.UNPROCESSABLE_ENTITY,
            message = "Validation for object ${exc.objectName} failed with ${exc.errorCount} error(s)",
            errors = exc.bindingResult.fieldErrors.associateBy({ it.field }, { it.defaultMessage ?: "" }),
            path = (request as ServletWebRequest).request.requestURI,
            timestamp = ZonedDateTime.now()
        )

        return handleExceptionInternal(
            exc,
            info,
            HttpHeaders(),
            info.status,
            request
        )
    }
}
