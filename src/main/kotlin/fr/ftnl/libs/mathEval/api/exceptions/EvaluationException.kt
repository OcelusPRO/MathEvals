package fr.ftnl.libs.mathEval.api.exceptions

/**
 * Exception for mathematical evaluation errors.
 * 
 * This exception is thrown when a mathematical error occurs during evaluation,
 * such as division by zero, invalid domain for a function, or overflow.
 */
class EvaluationException(
    message: String,
    cause: Throwable? = null
) : MathEvalException(message, cause)