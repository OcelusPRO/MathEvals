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

package fr.ftnl.tools.mathEval.core.tokenizer

/**
 * Token types in mathematical expressions.
 * 
 * This enum defines all possible types of tokens that can be
 * extracted from a mathematical expression.
 */
enum class TokenType {
    NUMBER,           // 123, 3.14, 2.5e-3
    OPERATOR,         // +, -, *, /, ^, %
    FUNCTION,         // sin, cos, tan, log, sqrt, etc.
    CONSTANT,         // pi, e
    VARIABLE,         // x, y, z, etc.
    LPAREN,           // (
    RPAREN,           // )
    COMMA,            // ,
    ERROR
}