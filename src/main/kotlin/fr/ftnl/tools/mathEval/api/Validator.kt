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
import fr.ftnl.tools.mathEval.core.tokenizer.TokenizationException

/**
 * Interface for validating mathematical expressions.
 * 
 * This interface defines the contract for classes that validate the syntax of
 * mathematical expressions and convert them into tokens for evaluation.
 */
interface Validator {
    /**
     * Validates and tokenizes a mathematical expression.
     * 
     * @param expression The mathematical expression to validate and tokenize.
     * @return A list of tokens representing the validated expression.
     * @throws TokenizationException If the expression contains syntax errors.
     */
    @Throws(TokenizationException::class)
    fun validate(expression: String): List<Token>
}
