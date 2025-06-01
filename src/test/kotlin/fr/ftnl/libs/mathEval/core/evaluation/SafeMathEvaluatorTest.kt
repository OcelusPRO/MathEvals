package fr.ftnl.libs.mathEval.core.evaluation

import fr.ftnl.libs.mathEval.core.tokenizer.Token
import fr.ftnl.libs.mathEval.core.tokenizer.TokenType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.assertContains

/**
 * Tests for the SafeMathEvaluator class.
 * 
 * This class tests the safety features of the SafeMathEvaluator, including:
 * - Protection against overly complex expressions
 * - Protection against excessively large exponents
 * - Proper evaluation of valid expressions
 * - Error handling for mathematical errors
 */
class SafeMathEvaluatorTest {

    private val evaluator = SafeMathEvaluator()
    private val delta = 1e-10 // Tolerance for floating-point comparisons

    /**
     * Utility function to create tokens for testing.
     */
    private fun createTokens(vararg tokenData: Pair<TokenType, String>): List<Token> {
        return tokenData.mapIndexed { index, (type, value) ->
            Token(type, value, index)
        }
    }

    @Test
    fun `evaluateWithSafeguards should correctly evaluate valid expressions`() {
        // Given
        val tokens = createTokens(
            TokenType.NUMBER to "2",
            TokenType.OPERATOR to "+",
            TokenType.NUMBER to "3",
            TokenType.OPERATOR to "*",
            TokenType.NUMBER to "4"
        )

        // When
        val result = evaluator.evaluateWithSafeguards(tokens)

        // Then
        assertEquals(14.0, result, delta) // 2 + 3 * 4 = 2 + 12 = 14
    }

    @Test
    fun `evaluateWithSafeguards should throw exception for too complex expressions`() {
        // Given
        // Create a list of 1001 tokens (beyond the 1000 token limit)
        val tokens = List(1001) { index ->
            if (index % 2 == 0) {
                Token(TokenType.NUMBER, index.toString(), index)
            } else {
                Token(TokenType.OPERATOR, "+", index)
            }
        }

        // When/Then
        val exception = assertThrows<ArithmeticException> {
            evaluator.evaluateWithSafeguards(tokens)
        }

        // Then
        assertContains(exception.message ?: "", "Expression too complex")
    }

    @Test
    fun `evaluateWithSafeguards should throw exception for excessively large exponents`() {
        // Given
        val tokens = createTokens(
            TokenType.NUMBER to "2",
            TokenType.OPERATOR to "^",
            TokenType.NUMBER to "1001" // Exponent > 1000
        )

        // When/Then
        val exception = assertThrows<ArithmeticException> {
            evaluator.evaluateWithSafeguards(tokens)
        }

        // Then
        assertContains(exception.message ?: "", "Exponent too large")
    }

    @Test
    fun `evaluateWithSafeguards should correctly evaluate expressions with variables`() {
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
        val result = evaluator.evaluateWithSafeguards(tokens, variables)

        // Then
        assertEquals(16.0, result, delta) // x^2 + 2*x + 1 = 3^2 + 2*3 + 1 = 9 + 6 + 1 = 16
    }

    @Test
    fun `evaluateWithSafeguards should throw exception for division by zero`() {
        // Given
        val tokens = createTokens(
            TokenType.NUMBER to "1",
            TokenType.OPERATOR to "/",
            TokenType.NUMBER to "0"
        )

        // When/Then
        val exception = assertThrows<ArithmeticException> {
            evaluator.evaluateWithSafeguards(tokens)
        }

        // Then
        assertContains(exception.message ?: "", "Division by zero")
    }

    @Test
    fun `evaluateWithSafeguards should throw exception for logarithm of zero`() {
        // Given
        val tokens = createTokens(
            TokenType.FUNCTION to "log",
            TokenType.LPAREN to "(",
            TokenType.NUMBER to "0",
            TokenType.RPAREN to ")"
        )

        // When/Then
        val exception = assertThrows<ArithmeticException> {
            evaluator.evaluateWithSafeguards(tokens)
        }

        // Then
        assertContains(exception.message ?: "", "Argument out of domain for log")
    }

    @Test
    fun `evaluateWithSafeguards should correctly handle custom functions`() {
        // Given
        val tokens = createTokens(
            TokenType.FUNCTION to "double",
            TokenType.LPAREN to "(",
            TokenType.NUMBER to "5",
            TokenType.RPAREN to ")"
        )

        // Register a custom function that doubles its input
        evaluator.registerFunction("double", { x -> x * 2 })

        // When
        val result = evaluator.evaluateWithSafeguards(tokens)

        // Then
        assertEquals(10.0, result, delta)

        // Clean up
        evaluator.removeFunction("double")
    }

    @Test
    fun `evaluateWithSafeguards should handle nested function calls`() {
        // Given
        val tokens = createTokens(
            TokenType.FUNCTION to "sin",
            TokenType.LPAREN to "(",
            TokenType.FUNCTION to "cos",
            TokenType.LPAREN to "(",
            TokenType.NUMBER to "0",
            TokenType.RPAREN to ")",
            TokenType.RPAREN to ")"
        )

        // When
        val result = evaluator.evaluateWithSafeguards(tokens)

        // Then
        assertEquals(sin(cos(0.0)), result, delta)
    }

    // Note: Testing timeout would require a way to create an infinite loop or very long calculation
    // which is difficult to do in a unit test. We'll assume the timeout mechanism works as expected.
}