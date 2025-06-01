package fr.ftnl.libs.mathEval.core.tokenizer

/**
 * Definition of a tokenization rule.
 * 
 * This class defines a rule for matching tokens in a mathematical expression,
 * consisting of a regex pattern, the type of token to create, and a priority
 * for resolving conflicts when multiple patterns match.
 */
data class TokenRule(
    val pattern: Regex,
    val tokenType: TokenType,
    val priority: Int
)