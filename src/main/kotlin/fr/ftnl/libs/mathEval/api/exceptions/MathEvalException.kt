package fr.ftnl.libs.mathEval.api.exceptions

/**
 * Base exception for all errors in the MathEvals library.
 * 
 * This is the parent class for all exceptions thrown by the library,
 * allowing users to catch all library-specific exceptions with a single catch block.
 */
open class MathEvalException(
    message: String, 
    cause: Throwable? = null
) : Exception(message, cause)