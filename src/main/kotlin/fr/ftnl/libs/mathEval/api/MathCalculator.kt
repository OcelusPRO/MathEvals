package fr.ftnl.libs.mathEval.api

import fr.ftnl.libs.mathEval.api.exceptions.EvaluationException
import fr.ftnl.libs.mathEval.api.exceptions.SecurityException
import fr.ftnl.libs.mathEval.api.exceptions.SyntaxException
import fr.ftnl.libs.mathEval.core.tokenizer.Token
import fr.ftnl.libs.mathEval.core.tokenizer.TokenType
import fr.ftnl.libs.mathEval.core.tokenizer.TokenizationException
import fr.ftnl.libs.mathEval.core.validation.MathExpressionValidator
import fr.ftnl.libs.mathEval.core.evaluation.SafeMathEvaluator
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
class MathCalculator : Calculator {
    private val evaluator = SafeMathEvaluator()

    /**
     * Evaluates a mathematical expression and returns the result.
     * 
     * @param expression The mathematical expression to evaluate as a string.
     * @param variables A map of variable names to their values, if the expression contains variables.
     * @return The result of the evaluation as a Double.
     * 
     * @throws SyntaxException If the expression contains syntax errors.
     * @throws EvaluationException If a mathematical error occurs during evaluation
     *                            (division by zero, invalid domain, etc.).
     * @throws SecurityException If the evaluation is potentially dangerous or takes too long.
     * @throws IllegalArgumentException If the expression contains undefined variables or functions.
     */
    override fun calculate(expression: String, variables: Map<String, Double>): Double {
        try {
            // Step 1: Tokenization and validation
            val tokens = MathExpressionValidator.validateAndTokenize(expression)

            // Step 2: Process tokens to handle custom functions
            val processedTokens = processCustomFunctions(tokens)

            // Step 3: Insert implicit multiplication operators
            val processedTokensWithImplicitMultiplication = insertImplicitMultiplicationOperators(processedTokens)

            // Step 4: Secure evaluation
            return evaluator.evaluateWithSafeguards(processedTokensWithImplicitMultiplication, variables)
        } catch (e: TokenizationException) {
            throw SyntaxException(e.message ?: "Syntax error", e.position, e.invalidCharacter)
        } catch (e: ArithmeticException) {
            throw EvaluationException(e.message ?: "Evaluation error", e)
        } catch (e: TimeoutException) {
            throw SecurityException("Calculation timed out: ${e.message}", e)
        } catch (e: IllegalArgumentException) {
            throw EvaluationException(e.message ?: "Invalid argument", e)
        }
    }

    private fun insertImplicitMultiplicationOperators(tokens: List<Token>): List<Token> {
        val result = mutableListOf<Token>()

        for (i in tokens.indices) {
            val current = tokens[i]
            result.add(current)

            // Skip if this is the last token
            if (i == tokens.lastIndex) continue

            val next = tokens[i + 1]

            // Cases where implicit multiplication should be inserted:
            val needsMultiplication = when {
                // Case 1: Number followed by opening parenthesis - e.g., "5(x+2)"
                current.type == TokenType.NUMBER && next.type == TokenType.LPAREN -> true

                // Case 2: Number followed by variable or constant - e.g., "2x" or "5pi"
                current.type == TokenType.NUMBER &&
                        (next.type == TokenType.VARIABLE || next.type == TokenType.CONSTANT) -> true

                // Case 3: Closing parenthesis followed by opening parenthesis - e.g., "(x+1)(x-1)"
                current.type == TokenType.RPAREN && next.type == TokenType.LPAREN -> true

                // Case 4: Closing parenthesis followed by number, variable or constant - e.g., "(x+1)2" or "(x+1)y"
                current.type == TokenType.RPAREN &&
                        (next.type == TokenType.NUMBER || next.type == TokenType.VARIABLE ||
                                next.type == TokenType.CONSTANT) -> true


                // Case 5: Variable or constant followed by opening parenthesis - e.g., "x(y+1)"
                (current.type == TokenType.VARIABLE || current.type == TokenType.CONSTANT) &&
                        next.type == TokenType.LPAREN -> true

                else -> false
            }

            if (needsMultiplication) {
                // Insert a multiplication operator between the tokens
                result.add(Token(
                    type = TokenType.OPERATOR,
                    value = "*",
                    position = current.position + current.value.length
                ))
            }
        }

        return result
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
    override fun registerFunction(name: String, function: MathFunction): Calculator {
        evaluator.registerFunction(name, function::apply)
        return this
    }

    /**
     * Removes a previously registered custom function.
     *
     * @param name The name of the function to remove.
     * @return This calculator instance for method chaining.
     */
    override fun removeFunction(name: String): Calculator {
        evaluator.removeFunction(name)
        return this
    }

    /**
     * Checks if a function with the given name is registered.
     *
     * @param name The name of the function to check.
     * @return true if the function is registered (either built-in or custom), false otherwise.
     */
    override fun hasFunction(name: String): Boolean {
        return evaluator.hasFunction(name)
    }

    /**
     * Utility method to display the tokens of an expression for debugging purposes.
     *
     * @param expression The mathematical expression to tokenize.
     * @return A list of tokens representing the expression.
     * @throws SyntaxException If the expression contains syntax errors.
     */
    override fun showTokens(expression: String): List<Token> {
        try {
            return MathExpressionValidator.validateAndTokenize(expression)
        } catch (e: TokenizationException) {
            throw SyntaxException(e.message ?: "Syntax error", e.position, e.invalidCharacter)
        }
    }

    /**
     * Utility method to display the tokens of an expression with implicit multiplication for debugging purposes.
     *
     * @param expression The mathematical expression to tokenize.
     * @return A list of tokens representing the expression with implicit multiplication operators inserted.
     * @throws SyntaxException If the expression contains syntax errors.
     */
    fun showImplicitTokens(expression: String): List<Token> {
        try {
            // Step 1: Tokenization and validation
            val tokens = MathExpressionValidator.validateAndTokenize(expression)

            // Step 2: Process tokens to handle custom functions
            val processedTokens = processCustomFunctions(tokens)

            // Step 3: Insert implicit multiplication operators
            return insertImplicitMultiplicationOperators(processedTokens)
        } catch (e: TokenizationException) {
            throw SyntaxException(e.message ?: "Syntax error", e.position, e.invalidCharacter)
        }
    }
}
