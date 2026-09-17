package de.partspicker.web.common.security.exceptions

class MissingClaimException(claimName: String) :
    RuntimeException("The given token is missing the required claim with name '$claimName'")
