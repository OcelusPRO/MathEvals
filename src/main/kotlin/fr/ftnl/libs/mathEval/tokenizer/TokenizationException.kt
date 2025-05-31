package fr.ftnl.libs.mathEval.tokenizer

/**
 * Exception personnalisée pour les erreurs de tokenisation
 */
class TokenizationException(
    message: String,
    val position: Int,
    val invalidCharacter: Char?
) : Exception("Tokenization exception at position $position: $message")

