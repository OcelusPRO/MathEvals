package fr.ftnl.libs.mathEval.api

import fr.ftnl.libs.mathEval.core.tokenizer.Token


/**
 * Interface for evaluating mathematical expressions.
 * 
 * This interface defines the contract for classes that evaluate tokenized
 * mathematical expressions and produce numerical results.
 */
interface Evaluator {
    /**
     * Evaluates a list of tokens representing a mathematical expression.
     * 
     * @param tokens The list of tokens to evaluate.
     * @param variables A map of variable names to their values, if the expression contains variables.
     * @return The result of the evaluation as a Double.
     * @throws ArithmeticException If a mathematical error occurs during evaluation.
     */
    @Throws(ArithmeticException::class)
    fun evaluate(tokens: List<Token>, variables: Map<String, Double> = emptyMap()): Double

    /**
     * Registers a custom function that can be used in expressions.
     *
     * @param name The name of the function as it will appear in expressions.
     * @param function The implementation of the function.
     * @return This evaluator instance for method chaining.
     */
    fun registerFunction(name: String, function: MathFunction): Evaluator

    /**
     * Removes a previously registered custom function.
     *
     * @param name The name of the function to remove.
     * @return This evaluator instance for method chaining.
     */
    fun removeFunction(name: String): Evaluator

    /**
     * Checks if a function with the given name is registered.
     *
     * @param name The name of the function to check.
     * @return true if the function is registered (either built-in or custom), false otherwise.
     */
    fun hasFunction(name: String): Boolean
}
