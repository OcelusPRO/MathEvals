package fr.ftnl.libs.mathEval.api.exceptions

/**
 * Exception for security-related errors in expression evaluation.
 * 
 * This exception is thrown when an expression is potentially dangerous,
 * such as expressions that would take too long to compute, use excessive resources,
 * or attempt to execute malicious operations.
 */
class SecurityException(
    message: String,
    cause: Throwable? = null
) : MathEvalException(message, cause)