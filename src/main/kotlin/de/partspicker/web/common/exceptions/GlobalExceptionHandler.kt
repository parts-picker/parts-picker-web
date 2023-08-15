package de.partspicker.web.common.exceptions

import de.partspicker.web.common.business.exceptions.RuleException
import de.partspicker.web.workflow.business.exceptions.WorkflowEdgeSourceNotMatchingException
import de.partspicker.web.workflow.business.exceptions.WorkflowInstanceNotActiveException
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
        value = [EntityNotFoundException::class],
    )
    fun handleEntityNotFoundException(
        exc: Exception,
        webRequest: ServletWebRequest,
    ): ResponseEntity<Any>? {
        val info = ErrorInfo(
            status = HttpStatus.NOT_FOUND,
            message = exc.localizedMessage,
            errorCode = ErrorCode.EntityNotFound,
            errors = mapOf(Pair(exc.javaClass.simpleName, exc.localizedMessage)),
            path = webRequest.request.requestURI,
            timestamp = ZonedDateTime.now(),
        )

        return handleExceptionInternal(
            exc,
            info,
            HttpHeaders(),
            info.status,
            webRequest,
        )
    }

    @ExceptionHandler(
        value = [
            WorkflowEdgeSourceNotMatchingException::class,
            WorkflowInstanceNotActiveException::class,
            WorkflowStartedWithNonStartNodeException::class,
        ],
    )
    fun handleWorkflowBusinessException(
        exc: Exception,
        webRequest: ServletWebRequest,
    ): ResponseEntity<Any>? {
        val info = ErrorInfo(
            status = HttpStatus.UNPROCESSABLE_ENTITY,
            message = exc.localizedMessage,
            errorCode = null,
            errors = mapOf(Pair(exc.javaClass.simpleName, exc.localizedMessage)),
            path = webRequest.request.requestURI,
            timestamp = ZonedDateTime.now(),
        )

        return handleExceptionInternal(
            exc,
            info,
            HttpHeaders(),
            info.status,
            webRequest,
        )
    }

    override fun handleMethodArgumentNotValid(
        exc: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val info = ErrorInfo(
            status = HttpStatus.UNPROCESSABLE_ENTITY,
            message = "Validation for object ${exc.objectName} failed with ${exc.errorCount} error(s)",
            errors = exc.bindingResult.fieldErrors.associateBy({ it.field }, { it.defaultMessage ?: "" }),
            path = (request as ServletWebRequest).request.requestURI,
            timestamp = ZonedDateTime.now(),
        )

        return handleExceptionInternal(
            exc,
            info,
            HttpHeaders(),
            info.status,
            request,
        )
    }

    @ExceptionHandler(
        value = [
            RuleException::class,
        ],
    )
    fun handleRuleException(
        exc: Exception,
        webRequest: ServletWebRequest,
    ): ResponseEntity<Any>? {
        val info = ErrorInfo(
            status = HttpStatus.UNPROCESSABLE_ENTITY,
            message = exc.localizedMessage,
            errorCode = null,
            errors = mapOf(Pair(exc.javaClass.simpleName, exc.localizedMessage)),
            path = webRequest.request.requestURI,
            timestamp = ZonedDateTime.now(),
        )

        return handleExceptionInternal(
            exc,
            info,
            HttpHeaders(),
            info.status,
            webRequest,
        )
    }
}
