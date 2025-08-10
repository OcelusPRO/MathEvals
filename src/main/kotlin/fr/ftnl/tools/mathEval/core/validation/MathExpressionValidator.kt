/**************************************************************************************************
 * MathEvals - MathEvals.main                                                                     *
 * Copyright (C) 2025 ocelus_ftnl                                                                 *
 *                                                                                                *
 * This program is free software: you can redistribute it and/or modify                           *
 * it under the terms of the GNU Affero General Public License as                                 *
 * published by the Free Software Foundation, either version 3 of the                             *
 * License, or (at your option) any later version.                                                *
 *                                                                                                *
 * This program is distributed in the hope that it will be useful,                                *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                  *
 * GNU Affero General Public License for more details.                                            *
 *                                                                                                *
 * You should have received a copy of the GNU Affero General Public License                       *
 * along with this program. If not, see <https://www.gnu.org/licenses/>.                          *
 **************************************************************************************************/

package fr.ftnl.tools.mathEval.core.validation

import fr.ftnl.tools.mathEval.api.Validator
import fr.ftnl.tools.mathEval.api.exceptions.SyntaxException
import fr.ftnl.tools.mathEval.core.tokenizer.MathExpressionTokenizer
import fr.ftnl.tools.mathEval.core.tokenizer.Token
import fr.ftnl.tools.mathEval.core.tokenizer.TokenType
import fr.ftnl.tools.mathEval.core.tokenizer.TokenizationException

/**
 * Validator for mathematical expressions.
 * 
 * This class provides methods to validate the syntax of mathematical expressions
 * and convert them into tokens for evaluation. It performs both pre-tokenization
 * validation (basic syntax) and post-tokenization validation (token sequence).
 */
class MathExpressionValidator : Validator {
    private val tokenizer = MathExpressionTokenizer()

    /**
     * Validates and tokenizes a mathematical expression.
     *
     * @param expression The mathematical expression to validate and tokenize.
     * @return A list of tokens representing the validated expression.
     * @throws SyntaxException If the expression contains syntax errors.
     */
    @Throws(SyntaxException::class)
    override fun validate(expression: String): List<Token> {
        try {
            // Preliminary validation
            validateBasicSyntax(expression)

            val tokens = tokenizer.tokenize(expression)

            // Post-tokenization validation
            validateTokenSequence(tokens)

            return tokens
        } catch (e: TokenizationException) {
            throw SyntaxException(e.message ?: "Syntax error", e.position, e.invalidCharacter)
        }
    }

    /**
     * Validates the basic syntax of an expression before tokenization.
     * 
     * Checks for:
     * - Empty expressions
     * - Balanced parentheses
     * - Allowed characters
     *
     * @param expression The expression to validate.
     * @throws TokenizationException If the expression contains basic syntax errors.
     */
    private fun validateBasicSyntax(expression: String) {
        if (expression.trim().isEmpty()) {
            throw TokenizationException("Empty expression", 0, null)
        }

        // Check for balanced parentheses
        var parenCount = 0
        for ((index, char) in expression.withIndex()) {
            when (char) {
                '(' -> parenCount++
                ')' -> {
                    parenCount--
                    if (parenCount < 0) {
                        throw TokenizationException(
                            "Closing parenthesis without matching opening parenthesis", index, char
                        )
                    }
                }
            }
        }

        if (parenCount > 0) {
            throw TokenizationException(
                "Unbalanced parentheses: $parenCount unclosed opening parenthesis", expression.length, null
            )
        }

        // Check for allowed characters
        val allowedChars = Regex("""[a-zA-Z0-9+\-*/^%(),.eE\s]""")
        for ((index, char) in expression.withIndex()) {
            if (!allowedChars.matches(char.toString())) {
                throw TokenizationException(
                    "Character not allowed in a mathematical expression", index, char
                )
            }
        }
    }

    /**
     * Validates the sequence of tokens after tokenization.
     * 
     * Checks for:
     * - Empty token lists
     * - Valid operator positions and sequences
     * - Function syntax
     * - Implicit multiplication
     *
     * @param tokens The list of tokens to validate.
     * @throws TokenizationException If the token sequence contains syntax errors.
     */
    private fun validateTokenSequence(tokens: List<Token>) {
        if (tokens.isEmpty()) {
            throw TokenizationException("Empty expression", 0, null)
        }

        for (i in tokens.indices) {
            val current = tokens[i]
            val next = tokens.getOrNull(i + 1)
            val previous = tokens.getOrNull(i - 1)

            when (current.type) {
                TokenType.OPERATOR -> {
                    // An operator cannot be at the beginning or end (except unary -)
                    if (i == 0 && current.value != "-") {
                        throw TokenizationException(
                            "Expression cannot start with the operator '${current.value}'", current.position, null
                        )
                    }
                    if (i == tokens.lastIndex) {
                        throw TokenizationException(
                            "Expression cannot end with an operator", current.position, null
                        )
                    }
                    // Two consecutive operators (except special cases)
                    if (next?.type == TokenType.OPERATOR &&
                        !(current.value == "-" && previous?.type in listOf(TokenType.LPAREN, TokenType.OPERATOR))) {
                        throw TokenizationException(
                            "Two consecutive operators detected", current.position, null
                        )
                    }
                }

                TokenType.FUNCTION -> {
                    // A function must be followed by an opening parenthesis
                    if (next?.type != TokenType.LPAREN) {
                        throw TokenizationException(
                            "Function '${current.value}' must be followed by '('", current.position, null
                        )
                    }
                }

                else -> { /* Other types are OK */ }
            }
        }
    }

    companion object {
        /**
         * Static method for backward compatibility.
         * 
         * @param expression The mathematical expression to validate and tokenize.
         * @return A list of tokens representing the validated expression.
         * @throws TokenizationException If the expression contains syntax errors.
         */
        @JvmStatic
        @Throws(TokenizationException::class)
        fun validateAndTokenize(expression: String): List<Token> {
            return MathExpressionValidator().validate(expression)
        }
    }
}
