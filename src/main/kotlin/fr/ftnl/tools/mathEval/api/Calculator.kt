/**************************************************************************************************
 * MathEvals - MathEvals.main                                                                     *
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

package fr.ftnl.tools.mathEval.api

import fr.ftnl.tools.mathEval.core.tokenizer.Token


/**
 * Interface for mathematical expression calculators.
 * 
 * This interface defines the contract for classes that evaluate mathematical expressions
 * from strings. It provides methods for calculation, function registration, and debugging.
 */
interface Calculator {
    /**
     * Evaluates a mathematical expression and returns the result.
     * 
     * @param expression The mathematical expression to evaluate as a string.
     * @param variables A map of variable names to their values, if the expression contains variables.
     * @return The result of the evaluation as a Double.
     * 
     * @throws Exception If the expression contains errors or cannot be evaluated.
     */
    fun calculate(expression: String, variables: Map<String, Double> = emptyMap()): Double

    /**
     * Registers a custom function that can be used in expressions.
     *
     * @param name The name of the function as it will appear in expressions.
     * @param function The implementation of the function.
     * @return This calculator instance for method chaining.
     */
    fun registerFunction(name: String, function: fr.ftnl.tools.mathEval.api.MathFunction): Calculator

    /**
     * Removes a previously registered custom function.
     *
     * @param name The name of the function to remove.
     * @return This calculator instance for method chaining.
     */
    fun removeFunction(name: String): Calculator

    /**
     * Checks if a function with the given name is registered.
     *
     * @param name The name of the function to check.
     * @return true if the function is registered (either built-in or custom), false otherwise.
     */
    fun hasFunction(name: String): Boolean

    /**
     * Utility method to display the tokens of an expression for debugging purposes.
     *
     * @param expression The mathematical expression to tokenize.
     * @return A list of tokens representing the expression.
     */
    fun showTokens(expression: String): List<fr.ftnl.tools.mathEval.core.tokenizer.Token>
}
