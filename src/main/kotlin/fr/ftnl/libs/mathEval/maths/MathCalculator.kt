package fr.ftnl.libs.mathEval.maths

import fr.ftnl.libs.mathEval.tokenizer.Token
import fr.ftnl.libs.mathEval.tokenizer.TokenType
import fr.ftnl.libs.mathEval.tokenizer.TokenizationException
import java.util.concurrent.TimeoutException

/**
 * Main entry point for the MathEvals library.
 * 
 * This class provides a simple interface for evaluating mathematical expressions
 * from strings. It handles the complete process of tokenization, validation, and
 * secure evaluation.
 * 
 * Example usage:
 * ```
 * val calculator = MathCalculator()
 * val result = calculator.calculate("2 + 3 * 4")  // Returns 14.0
 * ```
 * 
 * Custom functions can be registered:
 * ```
 * calculator.registerFunction("double", { x -> x * 2 })
 * val result = calculator.calculate("double(5)")  // Returns 10.0
 * ```
 */
class MathCalculator {
    private val evaluator = SafeMathEvaluator()

    /**
     * Evaluates a mathematical expression and returns the result.
     * 
     * @param expression The mathematical expression to evaluate as a string.
     * @param variables A map of variable names to their values, if the expression contains variables.
     * @return The result of the evaluation as a Double.
     * 
     * @throws TokenizationException If the expression contains syntax errors.
     * @throws ArithmeticException If a mathematical error occurs during evaluation
     *                            (division by zero, invalid domain, etc.).
     * @throws TimeoutException If the evaluation takes too long (protection against infinite loops).
     * @throws IllegalArgumentException If the expression contains undefined variables or functions.
     */
    @Throws(TokenizationException::class, ArithmeticException::class, 
            TimeoutException::class, IllegalArgumentException::class)
    fun calculate(expression: String, variables: Map<String, Double> = emptyMap()): Double {
        // Step 1: Tokenization and validation
        val tokens = MathExpressionValidator.validateAndTokenize(expression)

        // Step 2: Process tokens to handle custom functions
        val processedTokens = processCustomFunctions(tokens)

        // Step 3: Secure evaluation
        return evaluator.evaluateWithSafeguards(processedTokens, variables)
    }

    /**
     * Processes tokens to convert variables to functions if they are registered as custom functions.
     * This allows custom functions to be recognized by the tokenizer.
     *
     * @param tokens The original tokens from tokenization.
     * @return The processed tokens with custom functions properly identified.
     */
    private fun processCustomFunctions(tokens: List<Token>): List<Token> {
        return tokens.mapIndexed { index, token ->
            if (token.type == TokenType.VARIABLE && 
                evaluator.hasFunction(token.value) &&
                index < tokens.size - 1 && 
                tokens[index + 1].type == TokenType.LPAREN) {
                // This is a custom function followed by an opening parenthesis
                // Convert it from VARIABLE to FUNCTION
                Token(
                    type = TokenType.FUNCTION,
                    value = token.value,
                    position = token.position
                )
            } else {
                token
            }
        }
    }

    /**
     * Registers a custom function that can be used in expressions.
     *
     * @param name The name of the function as it will appear in expressions.
     * @param function The implementation of the function.
     * @return This calculator instance for method chaining.
     * @throws IllegalArgumentException If the function name is already used by a built-in function.
     */
    fun registerFunction(name: String, function: MathFunction): MathCalculator {
        evaluator.registerFunction(name, function)
        return this
    }

    /**
     * Removes a previously registered custom function.
     *
     * @param name The name of the function to remove.
     * @return This calculator instance for method chaining.
     */
    fun removeFunction(name: String): MathCalculator {
        evaluator.removeFunction(name)
        return this
    }

    /**
     * Checks if a function with the given name is registered.
     *
     * @param name The name of the function to check.
     * @return true if the function is registered (either built-in or custom), false otherwise.
     */
    fun hasFunction(name: String): Boolean {
        return evaluator.hasFunction(name)
    }

    /**
     * Utility method to display the tokens of an expression for debugging purposes.
     * 
     * @param expression The mathematical expression to tokenize.
     * @return A list of tokens representing the expression.
     * @throws TokenizationException If the expression contains syntax errors.
     */
    @Throws(TokenizationException::class)
    fun showTokens(expression: String): List<Token> {
        return MathExpressionValidator.validateAndTokenize(expression)
    }
}
