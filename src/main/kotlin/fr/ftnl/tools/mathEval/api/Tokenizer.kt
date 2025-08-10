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

import fr.ftnl.tools.mathEval.api.exceptions.SyntaxException
import fr.ftnl.tools.mathEval.core.tokenizer.Token

/**
 * Interface for tokenizing mathematical expressions.
 * 
 * This interface defines the contract for classes that convert mathematical expression
 * strings into tokens that can be processed by evaluators.
 */
interface Tokenizer {
    /**
     * Tokenizes a mathematical expression string into a list of tokens.
     * 
     * @param input The mathematical expression to tokenize.
     * @return A list of tokens representing the expression.
     * @throws SyntaxException If the expression contains syntax errors.
     */
    @Throws(SyntaxException::class)
    fun tokenize(input: String): List<Token>
}
