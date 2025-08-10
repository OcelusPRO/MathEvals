/**************************************************************************************************
 * MathEvals - MathEvals.test                                                                     *
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

import fr.ftnl.tools.mathEval.api.MathCalculator
import fr.ftnl.tools.mathEval.api.exceptions.SyntaxException
import fr.ftnl.tools.mathEval.core.tokenizer.TokenType
import fr.ftnl.tools.mathEval.core.tokenizer.TokenizationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains
import kotlin.test.assertEquals

class MathExpressionValidatorTest {

    @Test
    fun `validateAndTokenize should correctly tokenize valid expression`() {
        // Given
        val expression = "2 + 3 * 4"

        // When
        val tokens = MathExpressionValidator.validateAndTokenize(expression)

        // Then
        assertEquals(5, tokens.size)

        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("2", tokens[0].value)

        assertEquals(TokenType.OPERATOR, tokens[1].type)
        assertEquals("+", tokens[1].value)

        assertEquals(TokenType.NUMBER, tokens[2].type)
        assertEquals("3", tokens[2].value)

        assertEquals(TokenType.OPERATOR, tokens[3].type)
        assertEquals("*", tokens[3].value)

        assertEquals(TokenType.NUMBER, tokens[4].type)
        assertEquals("4", tokens[4].value)
    }

    @Test
    fun `validateAndTokenize should throw exception for empty expression`() {
        // Given
        val expression = ""

        // When/Then
        val exception = assertThrows<SyntaxException> {
            MathExpressionValidator.validateAndTokenize(expression)
        }

        // Then
        assertContains(exception.message ?: "", "Tokenization exception at position")
        assertContains(exception.message ?: "", "Empty expression")
    }

    @Test
    fun `validateAndTokenize should throw exception for unbalanced parentheses`() {
        // Given
        val expression = "(1 + 2"

        // When/Then
        val exception = assertThrows<SyntaxException> {
            MathExpressionValidator.validateAndTokenize(expression)
        }

        // Then
        assertContains(exception.message ?: "", "Tokenization exception at position 6: Unbalanced parentheses: 1 unclosed opening parenthesis" )
    }

    @Test
    fun `validateAndTokenize should throw exception for extra closing parenthesis`() {
        // Given
        val expression = "1 + 2)"

        // When/Then
        val exception = assertThrows<SyntaxException> {
            MathExpressionValidator.validateAndTokenize(expression)
        }

        // Then
        assertContains(exception.message ?: "", "Tokenization exception at position")
        assertContains(exception.message ?: "", "Closing parenthesis without matching opening parenthesis")
    }

    @Test
    fun `validateAndTokenize should throw exception for invalid character`() {
        // Given
        val expression = "1 @ 2"

        // When/Then
        val exception = assertThrows<SyntaxException> {
            MathExpressionValidator.validateAndTokenize(expression)
        }

        // Then
        assertContains(exception.message ?: "", "Tokenization exception at position")
        assertContains(exception.message ?: "", "Character not allowed in a mathematical expression")
    }

    @Test
    fun `validateAndTokenize should throw exception for operator at end`() {
        // Given
        val expression = "1 + 2 +"

        // When/Then
        val exception = assertThrows<SyntaxException> {
            MathExpressionValidator.validateAndTokenize(expression)
        }

        // Then
        assertContains(exception.message ?: "", "Tokenization exception at position")
        assertContains(exception.message ?: "", "Expression cannot end with an operator")
    }

    @Test
    fun `validateAndTokenize should throw exception for consecutive operators`() {
        // Given
        val expression = "1 + + 2"

        // When/Then
        val exception = assertThrows<SyntaxException> {
            MathExpressionValidator.validateAndTokenize(expression)
        }

        // Then
        assertContains(exception.message ?: "", "Tokenization exception at position")
        assertContains(exception.message ?: "", "Two consecutive operators detected")
    }

    @Test
    fun `validateAndTokenize should allow unary minus`() {
        // Given
        val expression = "-5 + 3"

        // When
        val tokens = MathExpressionValidator.validateAndTokenize(expression)

        // Then
        assertEquals(4, tokens.size)

        assertEquals(TokenType.OPERATOR, tokens[0].type)
        assertEquals("-", tokens[0].value)

        assertEquals(TokenType.NUMBER, tokens[1].type)
        assertEquals("5", tokens[1].value)

        assertEquals(TokenType.OPERATOR, tokens[2].type)
        assertEquals("+", tokens[2].value)

        assertEquals(TokenType.NUMBER, tokens[3].type)
        assertEquals("3", tokens[3].value)
    }
    
    @Test
    fun `validateAndTokenize should allow implicit multiplication after parentheses`() { // Given
        val expression = "(1+2)3"
        
        val tokens = MathCalculator().showImplicitTokens(expression)
        
        // When
        assertDoesNotThrow {
            MathExpressionValidator.validateAndTokenize(expression)
        }
        // Then
        assertEquals(7, tokens.size)
        
        assertEquals(TokenType.LPAREN, tokens[0].type)
        assertEquals("(", tokens[0].value)
        assertEquals(TokenType.NUMBER, tokens[1].type)
        assertEquals("1", tokens[1].value)
        assertEquals(TokenType.OPERATOR, tokens[2].type)
        assertEquals("+", tokens[2].value)
        assertEquals(TokenType.NUMBER, tokens[3].type)
        assertEquals("2", tokens[3].value)
        assertEquals(TokenType.RPAREN, tokens[4].type)
        assertEquals(")", tokens[4].value)
        assertEquals(TokenType.OPERATOR, tokens[5].type)
        assertEquals("*", tokens[5].value)
        assertEquals(TokenType.NUMBER, tokens[6].type)
        assertEquals("3", tokens[6].value)
        
        val result = MathCalculator().calculate(expression)
        assertEquals(9.0, result, 0.001) // (1 + 2) * 3 = 3 * 3 = 9
    }
}