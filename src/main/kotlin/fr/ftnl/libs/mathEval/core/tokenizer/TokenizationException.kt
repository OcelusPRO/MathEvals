package fr.ftnl.libs.mathEval.core.tokenizer

/**
 * Custom exception for tokenization errors.
 * 
 * This exception is thrown when an error occurs during the tokenization
 * of a mathematical expression, such as invalid characters or syntax.
 * It is used internally and converted to SyntaxException when exposed to API users.
 */
class TokenizationException(
    message: String,
    val position: Int,
    val invalidCharacter: Char?
) : Exception("Tokenization exception at position $position: $message")