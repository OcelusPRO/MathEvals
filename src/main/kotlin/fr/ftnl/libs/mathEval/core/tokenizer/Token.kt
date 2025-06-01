package fr.ftnl.libs.mathEval.core.tokenizer


/**
 * Representation of a token in the expression.
 * 
 * This class holds the type, value, and position of a token
 * extracted from a mathematical expression.
 */
data class Token(
    val type: TokenType,
    val value: String,
    val position: Int
)
