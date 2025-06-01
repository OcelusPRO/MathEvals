package fr.ftnl.libs.mathEval.api

import fr.ftnl.libs.mathEval.api.exceptions.SyntaxException
import fr.ftnl.libs.mathEval.core.tokenizer.Token

/**
 * Interface for tokenizing mathematical expressions.
 * 
 * This interface defines the contract for classes that convert mathematical expression
 * strings into tokens that can be processed by evaluators.
 */
interface Tokenizer {
    /**
     * Tokenizes a mathematical expression string into a list of tokens.
     * 
     * @param input The mathematical expression to tokenize.
     * @return A list of tokens representing the expression.
     * @throws SyntaxException If the expression contains syntax errors.
     */
    @Throws(SyntaxException::class)
    fun tokenize(input: String): List<Token>
}
