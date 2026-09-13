package de.partspicker.web.common.security.exceptions

class NoAuthenticatedUserException :
    RuntimeException("No authenticated user is present in the current security context")
