package fr.ftnl.libs.mathEval.core.tokenizer

import fr.ftnl.libs.mathEval.api.exceptions.SyntaxException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains

class MathExpressionTokenizerTest {

    private val tokenizer = MathExpressionTokenizer()

    @Test
    fun `tokenize should correctly tokenize simple expression`() {
        // Given
        val expression = "2 + 3"

        // When
        val tokens = tokenizer.tokenize(expression)

        // Then
        assertEquals(3, tokens.size)

        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("2", tokens[0].value)

        assertEquals(TokenType.OPERATOR, tokens[1].type)
        assertEquals("+", tokens[1].value)

        assertEquals(TokenType.NUMBER, tokens[2].type)
        assertEquals("3", tokens[2].value)
    }

    @Test
    fun `tokenize should correctly tokenize expression with parentheses`() {
        // Given
        val expression = "(1 + 2) * 3"

        // When
        val tokens = tokenizer.tokenize(expression)

        // Then
        assertEquals(7, tokens.size)

        assertEquals(TokenType.LPAREN, tokens[0].type)
        assertEquals(TokenType.NUMBER, tokens[1].type)
        assertEquals(TokenType.OPERATOR, tokens[2].type)
        assertEquals(TokenType.NUMBER, tokens[3].type)
        assertEquals(TokenType.RPAREN, tokens[4].type)
        assertEquals(TokenType.OPERATOR, tokens[5].type)
        assertEquals(TokenType.NUMBER, tokens[6].type)
    }

    @Test
    fun `tokenize should correctly tokenize expression with functions`() {
        // Given
        val expression = "sin(pi/2) + cos(0)"

        // When
        val tokens = tokenizer.tokenize(expression)

        // Then
        assertEquals(11, tokens.size)

        assertEquals(TokenType.FUNCTION, tokens[0].type)
        assertEquals("sin", tokens[0].value)

        assertEquals(TokenType.LPAREN, tokens[1].type)
        assertEquals(TokenType.CONSTANT, tokens[2].type)
        assertEquals("pi", tokens[2].value)

        assertEquals(TokenType.OPERATOR, tokens[3].type)
        assertEquals("/", tokens[3].value)

        assertEquals(TokenType.NUMBER, tokens[4].type)
        assertEquals("2", tokens[4].value)

        assertEquals(TokenType.RPAREN, tokens[5].type)
        assertEquals(TokenType.OPERATOR, tokens[6].type)
        assertEquals("+", tokens[6].value)

        assertEquals(TokenType.FUNCTION, tokens[7].type)
        assertEquals("cos", tokens[7].value)

        assertEquals(TokenType.LPAREN, tokens[8].type)
        assertEquals(TokenType.NUMBER, tokens[9].type)
        assertEquals("0", tokens[9].value)

        assertEquals(TokenType.RPAREN, tokens[10].type)
    }

    @Test
    fun `tokenize should correctly tokenize expression with variables`() {
        // Given
        val expression = "x^2 + 2*x + 1"

        // When
        val tokens = tokenizer.tokenize(expression)

        // Then
        assertEquals(9, tokens.size)

        assertEquals(TokenType.VARIABLE, tokens[0].type)
        assertEquals("x", tokens[0].value)

        assertEquals(TokenType.OPERATOR, tokens[1].type)
        assertEquals("^", tokens[1].value)

        assertEquals(TokenType.NUMBER, tokens[2].type)
        assertEquals("2", tokens[2].value)

        assertEquals(TokenType.OPERATOR, tokens[3].type)
        assertEquals("+", tokens[3].value)

        assertEquals(TokenType.NUMBER, tokens[4].type)
        assertEquals("2", tokens[4].value)

        assertEquals(TokenType.OPERATOR, tokens[5].type)
        assertEquals("*", tokens[5].value)

        assertEquals(TokenType.VARIABLE, tokens[6].type)
        assertEquals("x", tokens[6].value)

        assertEquals(TokenType.OPERATOR, tokens[7].type)
        assertEquals("+", tokens[7].value)

        assertEquals(TokenType.NUMBER, tokens[8].type)
        assertEquals("1", tokens[8].value)
    }

    @Test
    fun `tokenize should correctly tokenize scientific notation`() {
        // Given
        val expression = "2.5e-3 * 1000"

        // When
        val tokens = tokenizer.tokenize(expression)

        // Then
        assertEquals(3, tokens.size)

        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("2.5e-3", tokens[0].value)

        assertEquals(TokenType.OPERATOR, tokens[1].type)
        assertEquals("*", tokens[1].value)

        assertEquals(TokenType.NUMBER, tokens[2].type)
        assertEquals("1000", tokens[2].value)
    }

    @Test
    fun `tokenize should throw exception for invalid character`() {
        // Given
        val expression = "3 @ 4"

        // When/Then
        val exception = assertThrows<SyntaxException> {
            tokenizer.tokenize(expression)
        }

        // Then
        assertEquals('@', exception.invalidCharacter)
        assertContains(exception.message ?: "", "Invalid character '@'")
    }
}