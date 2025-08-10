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

package fr.ftnl.tools.mathEval.core.evaluation

import fr.ftnl.tools.mathEval.core.tokenizer.Token
import fr.ftnl.tools.mathEval.core.tokenizer.TokenType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains
import kotlin.test.assertEquals

class MathExpressionEvaluatorTest {

    private val evaluator = MathExpressionEvaluator()
    private val delta = 1e-10 // Tolerance for floating-point comparisons
    
    // Utility function to create tokens for tests
    private fun createTokens(vararg tokenData: Pair<TokenType, String>): List<Token> {
        return tokenData.mapIndexed { index, (type, value) ->
            Token(type, value, index)
        }
    }

    @Test
    fun `evaluate should correctly evaluate simple expressions`() {
        // Given
        val tokens1 = createTokens(
            TokenType.NUMBER to "2",
            TokenType.OPERATOR to "+",
            TokenType.NUMBER to "3",
            TokenType.OPERATOR to "*",
            TokenType.NUMBER to "4"
        )
        
        val tokens2 = createTokens(
            TokenType.NUMBER to "2",
            TokenType.OPERATOR to "*",
            TokenType.LPAREN to "(",
            TokenType.NUMBER to "3",
            TokenType.OPERATOR to "+",
            TokenType.NUMBER to "4",
            TokenType.RPAREN to ")"
        )
        
        // When
        val result1 = evaluator.evaluate(tokens1)
        val result2 = evaluator.evaluate(tokens2)
        
        // Then
        assertEquals(14.0, result1, delta) // 2 + 3 * 4 = 2 + 12 = 14
        assertEquals(14.0, result2, delta) // 2 * (3 + 4) = 2 * 7 = 14
    }
    
    @Test
    fun `evaluate should correctly evaluate expressions with functions`() {
        // Given
        val tokens1 = createTokens(
            TokenType.FUNCTION to "sin",
            TokenType.LPAREN to "(",
            TokenType.CONSTANT to "pi",
            TokenType.OPERATOR to "/",
            TokenType.NUMBER to "2",
            TokenType.RPAREN to ")"
        )
        
        val tokens2 = createTokens(
            TokenType.FUNCTION to "cos",
            TokenType.LPAREN to "(",
            TokenType.NUMBER to "0",
            TokenType.RPAREN to ")"
        )
        
        // When
        val result1 = evaluator.evaluate(tokens1)
        val result2 = evaluator.evaluate(tokens2)
        
        // Then
        assertEquals(1.0, result1, delta) // sin(pi/2) = 1
        assertEquals(1.0, result2, delta) // cos(0) = 1
    }
    
    @Test
    fun `evaluate should correctly evaluate expressions with exponents`() {
        // Given
        val tokens = createTokens(
            TokenType.NUMBER to "2",
            TokenType.OPERATOR to "^",
            TokenType.NUMBER to "3"
        )
        
        // When
        val result = evaluator.evaluate(tokens)
        
        // Then
        assertEquals(8.0, result, delta) // 2^3 = 8
    }
    
    @Test
    fun `evaluate should correctly evaluate expressions with variables`() {
        // Given
        val tokens = createTokens(
            TokenType.VARIABLE to "x",
            TokenType.OPERATOR to "^",
            TokenType.NUMBER to "2",
            TokenType.OPERATOR to "+",
            TokenType.NUMBER to "2",
            TokenType.OPERATOR to "*",
            TokenType.VARIABLE to "x",
            TokenType.OPERATOR to "+",
            TokenType.NUMBER to "1"
        )
        
        val variables = mapOf("x" to 3.0)
        
        // When
        val result = evaluator.evaluate(tokens, variables)
        
        // Then
        assertEquals(16.0, result, delta) // x^2 + 2*x + 1 = 3^2 + 2*3 + 1 = 9 + 6 + 1 = 16
    }
    
    @Test
    fun `evaluate should throw exception for division by zero`() {
        // Given
        val tokens = createTokens(
            TokenType.NUMBER to "1",
            TokenType.OPERATOR to "/",
            TokenType.NUMBER to "0"
        )
        
        // When/Then
        val exception = assertThrows<ArithmeticException> {
            evaluator.evaluate(tokens)
        }
        
        // Then
        assertEquals("Division by zero", exception.message)
    }
    
    @Test
    fun `evaluate should throw exception for logarithm of zero`() {
        // Given
        val tokens = createTokens(
            TokenType.FUNCTION to "log",
            TokenType.LPAREN to "(",
            TokenType.NUMBER to "0",
            TokenType.RPAREN to ")"
        )
        
        // When/Then
        val exception = assertThrows<ArithmeticException> {
            evaluator.evaluate(tokens)
        }
        
        // Then
        assertContains(exception.message ?: "", "Argument out of domain for log")
    }
    
    @Test
    fun `evaluate should throw exception for undefined variable`() {
        // Given
        val tokens = createTokens(
            TokenType.VARIABLE to "x",
            TokenType.OPERATOR to "+",
            TokenType.NUMBER to "1"
        )
        
        // When/Then
        val exception = assertThrows<IllegalArgumentException> {
            evaluator.evaluate(tokens)
        }
        
        // Then
        assertContains(exception.message ?: "", "Undefined variable: ")
    }
    
    @Test
    fun `evaluate should correctly handle unary minus`() {
        // Given
        val tokens = createTokens(
            TokenType.OPERATOR to "-",
            TokenType.NUMBER to "5"
        )
        
        // When
        val result = evaluator.evaluate(tokens)
        
        // Then
        assertEquals(-5.0, result, delta)
    }
}