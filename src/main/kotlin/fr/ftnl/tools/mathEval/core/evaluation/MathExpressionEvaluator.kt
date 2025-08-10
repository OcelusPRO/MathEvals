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

package fr.ftnl.tools.mathEval.core.evaluation

import fr.ftnl.tools.mathEval.api.Evaluator
import fr.ftnl.tools.mathEval.api.MathFunction
import fr.ftnl.tools.mathEval.core.tokenizer.Token
import fr.ftnl.tools.mathEval.core.tokenizer.TokenType
import kotlin.math.ln

/**
 * Base evaluator for mathematical expressions.
 * 
 * This class implements the core algorithm for evaluating mathematical expressions
 * using the Shunting Yard algorithm to convert infix notation to Reverse Polish Notation (RPN),
 * followed by RPN evaluation.
 * 
 * It supports:
 * - Basic arithmetic operations (+, -, *, /, ^, %)
 * - Mathematical functions (sin, cos, log, etc.)
 * - Variables and constants
 * - Proper operator precedence and associativity
 * - Custom user-defined functions
 */
open class MathExpressionEvaluator : Evaluator {

    /**
     * Map of custom functions that can be used in expressions.
     * The key is the function name, and the value is the function implementation.
     */
    protected val customFunctions: MutableMap<String, (Double) -> Double> = mutableMapOf()

    /**
     * Registers a custom function that can be used in expressions.
     *
     * @param name The name of the function as it will appear in expressions.
     * @param function The implementation of the function.
     * @return This evaluator instance for method chaining.
     * @throws IllegalArgumentException If the function name is already used by a built-in function.
     */
    override fun registerFunction(name: String, function: MathFunction): Evaluator {
        // Check if the name conflicts with a built-in function
        if (isBuiltInFunction(name)) {
            throw IllegalArgumentException("Cannot override built-in function: $name")
        }

        customFunctions[name] = function::apply
        return this
    }

    /**
     * Removes a previously registered custom function.
     *
     * @param name The name of the function to remove.
     * @return This evaluator instance for method chaining.
     */
    override fun removeFunction(name: String): Evaluator {
        customFunctions.remove(name)
        return this
    }

    /**
     * Checks if a function with the given name is registered.
     *
     * @param name The name of the function to check.
     * @return true if the function is registered (either built-in or custom), false otherwise.
     */
    override fun hasFunction(name: String): Boolean {
        return isBuiltInFunction(name) || customFunctions.containsKey(name)
    }

    /**
     * Checks if a function is a built-in function.
     *
     * @param name The name of the function to check.
     * @return true if the function is built-in, false otherwise.
     */
    private fun isBuiltInFunction(name: String): Boolean {
        return name in listOf(
            "sin", "cos", "tan", "asin", "acos", "atan",
            "log", "ln", "sqrt", "exp", "abs", "ceil", "floor"
        )
    }

    /**
     * Evaluates a tokenized mathematical expression.
     *
     * @param tokens The list of tokens representing the expression.
     * @param variables A map of variable names to their values.
     * @return The result of the evaluation as a Double.
     * @throws IllegalArgumentException If the expression contains undefined variables or functions,
     *                                 or if the expression is malformed.
     * @throws ArithmeticException If a mathematical error occurs during evaluation.
     */
    override fun evaluate(tokens: List<Token>, variables: Map<String, Double>): Double {
        // Convert to Reverse Polish Notation (RPN) for evaluation
        val rpnTokens = convertToRPN(tokens)
        return evaluateRPN(rpnTokens, variables)
    }

    /**
     * Converts an infix expression to Reverse Polish Notation (RPN) using the Shunting Yard algorithm.
     *
     * @param tokens The list of tokens in infix notation.
     * @return A list of tokens in RPN.
     * @throws IllegalArgumentException If the expression is malformed.
     */
    private fun convertToRPN(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val operatorStack = mutableListOf<Token>()

        for (token in tokens) {
            when (token.type) {
                TokenType.NUMBER, TokenType.VARIABLE, TokenType.CONSTANT -> {
                    output.add(token)
                }

                TokenType.FUNCTION -> {
                    operatorStack.add(token)
                }

                TokenType.COMMA -> {
                    // Pop operators until we find a left parenthesis
                    while (operatorStack.isNotEmpty() && operatorStack.last().type != TokenType.LPAREN) {
                        output.add(operatorStack.removeAt(operatorStack.lastIndex))
                    }

                    // If we didn't find a left parenthesis, there's a mismatched parenthesis
                    if (operatorStack.isEmpty() || operatorStack.last().type != TokenType.LPAREN) {
                        throw IllegalArgumentException("Mismatched parentheses or misplaced comma")
                    }
                }

                TokenType.OPERATOR -> {
                    // Handle unary minus
                    if (token.value == "-" && (output.isEmpty() || tokens.getOrNull(tokens.indexOf(token) - 1)?.type == TokenType.OPERATOR || tokens.getOrNull(tokens.indexOf(token) - 1)?.type == TokenType.LPAREN)) {
                        // This is a unary minus, treat it as a special case
                        // We'll push a 0 to the output and then the minus operator
                        output.add(Token(TokenType.NUMBER, "0", token.position))
                    }

                    // Pop operators with higher precedence
                    while (operatorStack.isNotEmpty() &&
                        operatorStack.last().type == TokenType.OPERATOR &&
                        (getOperatorPrecedence(operatorStack.last().value) > getOperatorPrecedence(token.value) ||
                                (getOperatorPrecedence(operatorStack.last().value) == getOperatorPrecedence(token.value) &&
                                        isLeftAssociative(token.value)))
                    ) {
                        output.add(operatorStack.removeAt(operatorStack.lastIndex))
                    }

                    operatorStack.add(token)
                }

                TokenType.LPAREN -> {
                    operatorStack.add(token)
                }

                TokenType.RPAREN -> {
                    // Pop operators until we find a left parenthesis
                    while (operatorStack.isNotEmpty() && operatorStack.last().type != TokenType.LPAREN) {
                        output.add(operatorStack.removeAt(operatorStack.lastIndex))
                    }

                    // If we didn't find a left parenthesis, there's a mismatched parenthesis
                    if (operatorStack.isEmpty() || operatorStack.last().type != TokenType.LPAREN) {
                        throw IllegalArgumentException("Mismatched parentheses")
                    }

                    // Remove the left parenthesis
                    operatorStack.removeAt(operatorStack.lastIndex)

                    // If the token at the top of the stack is a function, pop it onto the output queue
                    if (operatorStack.isNotEmpty() && operatorStack.last().type == TokenType.FUNCTION) {
                        output.add(operatorStack.removeAt(operatorStack.lastIndex))
                    }
                }

                else -> throw IllegalArgumentException("Unknown token type: ${token.type}")
            }
        }

        // Pop any remaining operators
        while (operatorStack.isNotEmpty()) {
            val op = operatorStack.removeAt(operatorStack.lastIndex)
            if (op.type == TokenType.LPAREN || op.type == TokenType.RPAREN) {
                throw IllegalArgumentException("Mismatched parentheses")
            }
            output.add(op)
        }

        return output
    }

    /**
     * Evaluates an expression in Reverse Polish Notation (RPN).
     *
     * @param tokens The list of tokens in RPN.
     * @param variables A map of variable names to their values.
     * @return The result of the evaluation as a Double.
     * @throws IllegalArgumentException If the expression contains undefined variables or functions,
     *                                 or if the expression is malformed.
     * @throws ArithmeticException If a mathematical error occurs during evaluation.
     */
    private fun evaluateRPN(tokens: List<Token>, variables: Map<String, Double>): Double {
        val stack = mutableListOf<Double>()

        for (token in tokens) {
            when (token.type) {
                TokenType.NUMBER -> {
                    stack.add(token.value.toDouble())
                }

                TokenType.VARIABLE -> {
                    val value = variables[token.value]
                        ?: throw IllegalArgumentException("Undefined variable: ${token.value}")
                    stack.add(value)
                }

                TokenType.CONSTANT -> {
                    val value = when (token.value) {
                        "pi" -> Math.PI
                        "e" -> Math.E
                        else -> throw IllegalArgumentException("Unknown constant: ${token.value}")
                    }
                    stack.add(value)
                }

                TokenType.OPERATOR -> {
                    if (stack.size < 2) {
                        throw IllegalArgumentException("Invalid expression: not enough operands for operator ${token.value}")
                    }

                    val b = stack.removeAt(stack.lastIndex)
                    val a = stack.removeAt(stack.lastIndex)

                    val result = when (token.value) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> {
                            if (b == 0.0) throw ArithmeticException("Division by zero")
                            a / b
                        }
                        "^", "**" -> Math.pow(a, b)
                        "%" -> {
                            if (b == 0.0) throw ArithmeticException("Modulo by zero")
                            a % b
                        }
                        else -> throw IllegalArgumentException("Unknown operator: ${token.value}")
                    }

                    stack.add(result)
                }

                TokenType.FUNCTION -> {
                    if (stack.isEmpty()) {
                        throw IllegalArgumentException("Invalid expression: not enough arguments for function ${token.value}")
                    }

                    val arg = stack.removeAt(stack.lastIndex)
                    val result = evaluateFunction(token.value, arg)
                    stack.add(result)
                }

                else -> throw IllegalArgumentException("Unexpected token in RPN: ${token.type}")
            }
        }

        if (stack.size != 1) {
            throw IllegalArgumentException("Invalid expression: too many values left on the stack")
        }

        return stack[0]
    }

    /**
     * Evaluates a mathematical function.
     *
     * @param name The name of the function.
     * @param arg The argument to the function.
     * @return The result of the function evaluation.
     * @throws IllegalArgumentException If the function is undefined or if the argument is invalid.
     * @throws ArithmeticException If a mathematical error occurs during evaluation.
     */
    private fun evaluateFunction(name: String, arg: Double): Double {
        // Check for custom functions first
        customFunctions[name]?.let { return it(arg) }

        // Then check built-in functions
        return when (name) {
            "sin" -> Math.sin(arg)
            "cos" -> Math.cos(arg)
            "tan" -> Math.tan(arg)
            "asin" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Argument out of domain for asin: $arg")
                Math.asin(arg)
            }
            "acos" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Argument out of domain for acos: $arg")
                Math.acos(arg)
            }
            "atan" -> Math.atan(arg)
            "log" -> {
                if (arg <= 0.0) throw ArithmeticException("Argument out of domain for log: $arg")
                Math.log10(arg)
            }
            "ln" -> {
                if (arg <= 0.0) throw ArithmeticException("Argument out of domain for ln: $arg")
                ln(arg)
            }
            "sqrt" -> {
                if (arg < 0.0) throw ArithmeticException("Argument out of domain for sqrt: $arg")
                Math.sqrt(arg)
            }
            "exp" -> Math.exp(arg)
            "abs" -> Math.abs(arg)
            "ceil" -> Math.ceil(arg)
            "floor" -> Math.floor(arg)
            else -> throw IllegalArgumentException("Unknown function: $name")
        }
    }

    /**
     * Gets the precedence of an operator.
     * Higher values indicate higher precedence.
     *
     * @param operator The operator.
     * @return The precedence value.
     */
    private fun getOperatorPrecedence(operator: String): Int {
        return when (operator) {
            "+", "-" -> 1
            "*", "/", "%" -> 2
            "^", "**" -> 3
            else -> 0
        }
    }

    /**
     * Checks if an operator is left-associative.
     *
     * @param operator The operator.
     * @return true if the operator is left-associative, false otherwise.
     */
    private fun isLeftAssociative(operator: String): Boolean {
        return when (operator) {
            "+", "-", "*", "/", "%" -> true
            "^", "**" -> false
            else -> true
        }
    }
}