package fr.ftnl.libs.mathEval.tokenizer


/**
 * Représentation d'un token dans l'expression
 */
data class Token(
    val type: TokenType,
    val value: String,
    val position: Int
)
