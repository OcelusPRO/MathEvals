package fr.ftnl.libs.mathEval.tokenizer

/**
 * Définition d'une règle de tokenisation
 */
data class TokenRule(
    val pattern: Regex,
    val tokenType: TokenType,
    val priority: Int
)