package fr.ftnl.libs.mathEval.api

/**
 * Functional interface for mathematical functions.
 * 
 * This interface represents a mathematical function that takes a single Double parameter
 * and returns a Double result. It is used for registering custom functions with the calculator.
 */
fun interface MathFunction {
    /**
     * Applies the mathematical function to the given input.
     * 
     * @param x The input value.
     * @return The result of applying the function to the input.
     */
    fun apply(x: Double): Double
}