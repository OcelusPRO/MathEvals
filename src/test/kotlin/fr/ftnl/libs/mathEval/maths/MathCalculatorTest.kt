package fr.ftnl.libs.mathEval.maths

import fr.ftnl.libs.mathEval.tokenizer.TokenizationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class MathCalculatorTest {

    private val calculator = MathCalculator()
    private val delta = 1e-10 // Tolérance pour les comparaisons de nombres à virgule flottante

    @Test
    fun `calculate should correctly evaluate simple expressions`() {
        // Given
        val expression1 = "2 + 3 * 4"
        val expression2 = "2 * (3 + 4)"

        // When
        val result1 = calculator.calculate(expression1)
        val result2 = calculator.calculate(expression2)

        // Then
        assertEquals(14.0, result1, delta)
        assertEquals(14.0, result2, delta)
    }

    @Test
    fun `calculate should correctly evaluate expressions with functions`() {
        // Given
        val expression1 = "sin(pi/2)"
        val expression2 = "cos(0)"
        val expression3 = "sqrt(16) + log(100)"

        // When
        val result1 = calculator.calculate(expression1)
        val result2 = calculator.calculate(expression2)
        val result3 = calculator.calculate(expression3)

        // Then
        assertEquals(1.0, result1, delta)
        assertEquals(1.0, result2, delta)
        assertEquals(6.0, result3, delta)
    }

    @Test
    fun `calculate should correctly evaluate expressions with exponents`() {
        // Given
        val expression1 = "2^3"
        val expression2 = "2^(2+1)"
        val expression3 = "2^8"

        // When
        val result1 = calculator.calculate(expression1)
        val result2 = calculator.calculate(expression2)
        val result3 = calculator.calculate(expression3)

        // Then
        assertEquals(8.0, result1, delta)
        assertEquals(8.0, result2, delta)
        assertEquals(256.0, result3, delta)
    }
    
    @Test
    fun `calculate should correctly evaluate expressions with variables`() {
        // Given
        val expression = "x^2 + 2*x + 1"
        val variables = mapOf("x" to 3.0)
        
        // When
        val result = calculator.calculate(expression, variables)
        
        // Then
        assertEquals(16.0, result, delta) // 3^2 + 2*3 + 1 = 9 + 6 + 1 = 16
    }
    
    @Test
    fun `calculate should correctly evaluate expressions with multiples variables`() {
        // Given
        val expression = "x^2 + 2*y + 1"
        val variables = mapOf("x" to 3.0, "y" to 4.0)
        
        // When
        val result = calculator.calculate(expression, variables)
        
        // Then
        assertEquals(18.0, result, delta) // 3^2 + 2*3 + 1 = 9 + 8 + 1 = 18
    }

    @Test
    fun `calculate should correctly evaluate expressions with scientific notation`() {
        // Given
        val expression = "2.5e-3 * 1000"

        // When
        val result = calculator.calculate(expression)

        // Then
        assertEquals(2.5, result, delta)
    }

    @Test
    fun `calculate should throw exception for division by zero`() {
        // Given
        val expression = "1/0"

        // When/Then
        val exception = assertThrows<ArithmeticException> {
            calculator.calculate(expression)
        }

        // Then
        assertTrue(exception.message!!.contains("Division by zero"))
    }

    @Test
    fun `calculate should throw exception for logarithm of zero`() {
        // Given
        val expression = "log(0)"

        // When/Then
        val exception = assertThrows<ArithmeticException> {
            calculator.calculate(expression)
        }

        // Then
        assertTrue(exception.message!!.contains("Logarithm of a negative or zero number"))
    }

    @Test
    fun `calculate should throw exception for syntax error`() {
        // Given
        val expression = "1 + + 2"

        // When/Then
        val exception = assertThrows<TokenizationException> {
            calculator.calculate(expression)
        }

        // Then
        assertTrue(exception.message!!.contains("Two consecutive operators"))
    }

    @Test
    fun `calculate should throw exception for undefined variable`() {
        // Given
        val expression = "x + 1"

        // When/Then
        val exception = assertThrows<ArithmeticException> {
            calculator.calculate(expression)
        }

        // Then
        assertTrue(exception.message!!.contains("Undefined variable"))
    }

    @Test
    fun `showTokens should return tokens for valid expression`() {
        // Given
        val expression = "2 + 3"

        // When
        val tokens = calculator.showTokens(expression)

        // Then
        assertEquals(3, tokens.size)
    }

    @Test
    fun `calculate should correctly evaluate expressions with custom functions`() {
        // Given
        val calculator = MathCalculator()

        // Register a custom function that doubles its input
        calculator.registerFunction("double") { x -> x * 2 }
        
        // Register a custom function that converts degrees to radians
        calculator.registerFunction("toRadians") { degrees -> degrees * Math.PI / 180 }
        
        // When
        val result1 = calculator.calculate("double(5)")
        val result2 = calculator.calculate("sin(toRadians(90))")

        // Then
        assertEquals(10.0, result1, delta)
        assertEquals(1.0, result2, delta)

        // Verify function existence
        assertTrue(calculator.hasFunction("double"))
        assertTrue(calculator.hasFunction("toRadians"))
        assertTrue(calculator.hasFunction("sin"))  // Built-in function

        // Remove a custom function
        calculator.removeFunction("double")
        assertFalse(calculator.hasFunction("double"))

        // Verify that the removed function no longer works
        val exception = assertThrows<ArithmeticException> {
            calculator.calculate("double(5)")
        }
        assertTrue(exception.message!!.contains("Undefined variable"))
    }
}
