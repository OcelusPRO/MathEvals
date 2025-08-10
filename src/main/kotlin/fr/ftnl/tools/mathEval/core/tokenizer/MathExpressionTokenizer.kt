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

package fr.ftnl.tools.mathEval.core.tokenizer

import fr.ftnl.tools.mathEval.api.Tokenizer
import fr.ftnl.tools.mathEval.api.exceptions.SyntaxException
import fr.ftnl.tools.mathEval.core.tokenizer.TokenizationException

/**
 * Specialized tokenizer for mathematical expressions.
 * 
 * This class converts mathematical expression strings into tokens
 * that can be processed by evaluators.
 */
class MathExpressionTokenizer : Tokenizer {

    private val tokenRules = listOf(
        // Numbers (high priority to avoid confusion with variables)
        TokenRule(Regex("""\d+\.\d+[eE][+-]?\d+"""), TokenType.NUMBER, 10), // 2.5e-3
        TokenRule(Regex("""\d+[eE][+-]?\d+"""), TokenType.NUMBER, 9),        // 2e3
        TokenRule(Regex("""\d+\.\d+"""), TokenType.NUMBER, 8),               // 3.14
        TokenRule(Regex("""\d+"""), TokenType.NUMBER, 7),                    // 123

        // Mathematical functions (high priority before variables)
        TokenRule(Regex("""sin|cos|tan|asin|acos|atan"""), TokenType.FUNCTION, 6),
        TokenRule(Regex("""log|ln|exp|sqrt|abs|ceil|floor"""), TokenType.FUNCTION, 6),

        // Mathematical constants
        TokenRule(Regex("""pi|e"""), TokenType.CONSTANT, 5),

        // Operators (priority by length)
        TokenRule(Regex("""\*\*"""), TokenType.OPERATOR, 4), // ** (alternative power)
        TokenRule(Regex("""[+\-*/^%]"""), TokenType.OPERATOR, 3),

        // Parentheses and separators
        TokenRule(Regex("""\("""), TokenType.LPAREN, 2),
        TokenRule(Regex("""\)"""), TokenType.RPAREN, 2),
        TokenRule(Regex(""","""), TokenType.COMMA, 2),

        // Variables (lowest priority)
        TokenRule(Regex("""[a-zA-Z][a-zA-Z0-9]*"""), TokenType.VARIABLE, 1)
    )

    /**
     * Tokenizes a mathematical expression string into a list of tokens.
     * 
     * @param input The mathematical expression to tokenize.
     * @return A list of tokens representing the expression.
     * @throws SyntaxException If the expression contains syntax errors.
     */
    @Throws(SyntaxException::class)
    override fun tokenize(input: String): List<Token> {
        try {
            val cleanInput = input.replace(Regex("""\s+"""), "") // Remove spaces
            val initialTokens = mutableListOf<Token>()
            var position = 0

            while (position < cleanInput.length) {
                val token = findLongestMatch(cleanInput, position)
                    ?: throw TokenizationException(
                        "Invalid character '${cleanInput[position]}'",
                        position,
                        cleanInput[position]
                    )

                initialTokens.add(token)
                position += token.value.length
            }

            return initialTokens
        } catch (e: TokenizationException) {
            throw SyntaxException(e.message ?: "Syntax error", e.position, e.invalidCharacter)
        }
    }

    /**
     * Finds the longest matching token at the given position in the input string.
     * 
     * @param input The input string to search in.
     * @param startPosition The position to start searching from.
     * @return The longest matching token, or null if no match is found.
     */
    private fun findLongestMatch(input: String, startPosition: Int): Token? {
        var longestMatch: Token? = null
        var maxLength = 0

        for (rule in tokenRules.sortedByDescending { it.priority }) {
            val remainingInput = input.substring(startPosition)
            val matchResult = rule.pattern.find(remainingInput)

            if (matchResult != null && matchResult.range.first == 0) {
                val matchLength = matchResult.value.length

                // Maximal Munch principle: take the longest match
                if (matchLength > maxLength) {
                    maxLength = matchLength
                    longestMatch = Token(
                        type = rule.tokenType,
                        value = matchResult.value,
                        position = startPosition
                    )
                }
            }
        }

        return longestMatch
    }
}
