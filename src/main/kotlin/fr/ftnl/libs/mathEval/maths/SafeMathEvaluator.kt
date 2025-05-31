package fr.ftnl.libs.mathEval.maths

import fr.ftnl.libs.mathEval.tokenizer.Token
import fr.ftnl.libs.mathEval.tokenizer.TokenType
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Secure evaluator with protection against infinite loops and dangerous operations.
 * 
 * This class extends the base evaluator with additional safety features:
 * - Timeout for long-running calculations
 * - Validation for potentially dangerous operations
 * - Protection against excessively complex expressions
 */
class SafeMathEvaluator : MathExpressionEvaluator() {

    companion object {
        private const val COMPUTATION_TIMEOUT_MS = 5000L
    }

    /**
     * Evaluates an expression with additional safety checks.
     *
     * @param tokens The list of tokens representing the expression.
     * @param variables A map of variable names to their values.
     * @return The result of the evaluation as a Double.
     * @throws TimeoutException If the evaluation takes too long.
     * @throws ArithmeticException If a mathematical error occurs or if the expression is potentially dangerous.
     */
    @Throws(TimeoutException::class, ArithmeticException::class)
    fun evaluateWithSafeguards(tokens: List<Token>, variables: Map<String, Double> = emptyMap()): Double {
        validateForDangerousOperations(tokens)

        return runWithTimeout(COMPUTATION_TIMEOUT_MS) {
            super.evaluate(tokens, variables)
        }
    }

    /**
     * Validates the expression for potentially dangerous operations.
     *
     * @param tokens The list of tokens to validate.
     * @throws ArithmeticException If the expression is too complex or contains dangerous operations.
     */
    private fun validateForDangerousOperations(tokens: List<Token>) {
        // Check for overly complex expressions
        if (tokens.size > 1000) {
            throw ArithmeticException("Expression too complex (more than 1000 tokens)")
        }

        // Check for excessively large exponents
        tokens.forEachIndexed { index, token ->
            if ((token.type == TokenType.OPERATOR && (token.value == "^" || token.value == "**"))
                && index < tokens.lastIndex) {

                val nextToken = tokens[index + 1]
                if (nextToken.type == TokenType.NUMBER) {
                    val exponent = nextToken.value.toDoubleOrNull() ?: 0.0
                    if (exponent > 1000) {
                        throw ArithmeticException("Exponent too large: $exponent > 1000")
                    }
                }
            }
        }
    }

    /**
     * Runs a computation with a timeout to prevent infinite loops.
     *
     * @param timeoutMs The maximum time in milliseconds to allow the computation to run.
     * @param computation The computation to run.
     * @return The result of the computation.
     * @throws ArithmeticException If the computation times out or throws an exception.
     */
    private fun <T> runWithTimeout(timeoutMs: Long, computation: () -> T): T {
        val future = CompletableFuture.supplyAsync(computation)

        return try {
            future.get(timeoutMs, TimeUnit.MILLISECONDS)
        } catch (e: TimeoutException) {
            future.cancel(true)
            throw ArithmeticException("Calculation interrupted: timeout exceeded (${timeoutMs}ms)")
        } catch (e: ExecutionException) {
            throw e.cause as? ArithmeticException
                ?: ArithmeticException("Calculation error: ${e.cause?.message}")
        }
    }
}
