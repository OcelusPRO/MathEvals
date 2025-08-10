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
    fun evaluate(tokens: List<fr.ftnl.tools.mathEval.core.tokenizer.Token>, variables: Map<String, Double> = emptyMap()): Double

    /**
     * Registers a custom function that can be used in expressions.
     *
     * @param name The name of the function as it will appear in expressions.
     * @param function The implementation of the function.
     * @return This evaluator instance for method chaining.
     */
    fun registerFunction(name: String, function: fr.ftnl.tools.mathEval.api.MathFunction): Evaluator

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
