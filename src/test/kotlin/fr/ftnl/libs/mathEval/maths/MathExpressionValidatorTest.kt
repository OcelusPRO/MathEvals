package fr.ftnl.libs.mathEval.maths

import fr.ftnl.libs.mathEval.tokenizer.TokenType
import fr.ftnl.libs.mathEval.tokenizer.TokenizationException
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
        val exception = assertThrows<TokenizationException> {
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
        val exception = assertThrows<TokenizationException> {
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
        val exception = assertThrows<TokenizationException> {
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
        val exception = assertThrows<TokenizationException> {
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
        val exception = assertThrows<TokenizationException> {
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
        val exception = assertThrows<TokenizationException> {
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
    fun `validateAndTokenize should throw exception for implicit multiplication after parentheses`() {
        // Given
        val expression = "(1+2)3"
        
        // When/Then
        val exception = assertThrows<TokenizationException> {
            MathExpressionValidator.validateAndTokenize(expression)
        }
        
        // Then
        assertContains(exception.message ?: "", "Tokenization exception at position")
        assertContains(exception.message ?: "", "Implicit multiplication not allowed after ')'")
    }
    
    @Test
    fun `validateAndTokenize should does not throw exception for implicit multiplication before parentheses`() {
        // Given
        val expression = "3(1+2)"
        
        // When/Then
        assertDoesNotThrow {
            MathExpressionValidator.validateAndTokenize(expression)
        }
    }
}
