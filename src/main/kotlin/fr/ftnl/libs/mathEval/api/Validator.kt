package fr.ftnl.libs.mathEval.api

import fr.ftnl.libs.mathEval.core.tokenizer.Token
import fr.ftnl.libs.mathEval.core.tokenizer.TokenizationException

/**
 * Interface for validating mathematical expressions.
 * 
 * This interface defines the contract for classes that validate the syntax of
 * mathematical expressions and convert them into tokens for evaluation.
 */
interface Validator {
    /**
     * Validates and tokenizes a mathematical expression.
     * 
     * @param expression The mathematical expression to validate and tokenize.
     * @return A list of tokens representing the validated expression.
     * @throws TokenizationException If the expression contains syntax errors.
     */
    @Throws(TokenizationException::class)
    fun validate(expression: String): List<Token>
}
