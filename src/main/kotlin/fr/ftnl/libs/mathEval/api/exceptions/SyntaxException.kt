package fr.ftnl.libs.mathEval.api.exceptions

/**
 * Exception for syntax errors in mathematical expressions.
 * 
 * This exception is thrown when an expression contains syntax errors
 * such as unbalanced parentheses, invalid characters, or incorrect operator usage.
 */
class SyntaxException(
    message: String,
    val position: Int,
    val invalidCharacter: Char?
) : MathEvalException("Syntax error at position $position: $message")