package fr.ftnl.libs.mathEval.maths

import fr.ftnl.libs.mathEval.tokenizer.Token
import fr.ftnl.libs.mathEval.tokenizer.TokenType
import kotlin.math.ln

/**
 * Function type for custom mathematical functions.
 * Takes a Double argument and returns a Double result.
 */
typealias MathFunction = (Double) -> Double

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
open class MathExpressionEvaluator {

    /**
     * Map of custom functions that can be used in expressions.
     * The key is the function name, and the value is the function implementation.
     */
    protected val customFunctions: MutableMap<String, MathFunction> = mutableMapOf()

    /**
     * Registers a custom function that can be used in expressions.
     *
     * @param name The name of the function as it will appear in expressions.
     * @param function The implementation of the function.
     * @return This evaluator instance for method chaining.
     * @throws IllegalArgumentException If the function name is already used by a built-in function.
     */
    fun registerFunction(name: String, function: MathFunction): MathExpressionEvaluator {
        // Check if the name conflicts with a built-in function
        if (isBuiltInFunction(name)) {
            throw IllegalArgumentException("Cannot override built-in function: $name")
        }

        customFunctions[name] = function
        return this
    }

    /**
     * Removes a previously registered custom function.
     *
     * @param name The name of the function to remove.
     * @return This evaluator instance for method chaining.
     */
    fun removeFunction(name: String): MathExpressionEvaluator {
        customFunctions.remove(name)
        return this
    }

    /**
     * Checks if a function with the given name is registered.
     *
     * @param name The name of the function to check.
     * @return true if the function is registered (either built-in or custom), false otherwise.
     */
    fun hasFunction(name: String): Boolean {
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
    fun evaluate(tokens: List<Token>, variables: Map<String, Double> = emptyMap()): Double {
        // Convert to Reverse Polish Notation (RPN) for evaluation
        val rpnTokens = convertToRPN(tokens)
        return evaluateRPN(rpnTokens, variables)
    }

    /**
     * Converts an infix expression to Reverse Polish Notation using the Shunting Yard algorithm.
     *
     * @param tokens The list of tokens in infix notation.
     * @return The list of tokens in RPN.
     */
    private fun convertToRPN(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val operators = mutableListOf<Token>()

        for (token in tokens) {
            when (token.type) {
                TokenType.NUMBER, TokenType.VARIABLE, TokenType.CONSTANT -> {
                    output.add(token)
                }

                TokenType.FUNCTION -> {
                    operators.add(token)
                }

                TokenType.OPERATOR -> {
                    while (operators.isNotEmpty() &&
                        operators.last().type != TokenType.LPAREN &&
                        hasHigherPrecedence(operators.last(), token)) {
                        output.add(operators.removeAt(operators.lastIndex))
                    }
                    operators.add(token)
                }

                TokenType.LPAREN -> {
                    operators.add(token)
                }

                TokenType.RPAREN -> {
                    while (operators.isNotEmpty() && operators.last().type != TokenType.LPAREN) {
                        output.add(operators.removeAt(operators.lastIndex))
                    }
                    if (operators.isNotEmpty()) {
                        operators.removeAt(operators.lastIndex) // Remove '('
                    }

                    // If a function precedes '(', add it to the output
                    if (operators.isNotEmpty() && operators.last().type == TokenType.FUNCTION) {
                        output.add(operators.removeAt(operators.lastIndex))
                    }
                }

                else -> { /* Ignore other types */ }
            }
        }

        // Empty the operator stack
        while (operators.isNotEmpty()) {
            output.add(operators.removeAt(operators.lastIndex))
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
     * @throws ArithmeticException If a mathematical error occurs during evaluation
     *                            (division by zero, invalid domain, etc.).
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
                    stack.add(when (token.value) {
                        "pi" -> Math.PI
                        "e" -> Math.E
                        else -> throw IllegalArgumentException("Unknown constant: ${token.value}")
                    })
                }

                TokenType.OPERATOR -> {
                    if (stack.size < 2 && token.value != "-") {
                        throw IllegalArgumentException("Operator ${token.value} requires 2 operands")
                    }

                    val result = when (token.value) {
                        "+" -> {
                            val b = stack.removeAt(stack.lastIndex)
                            val a = stack.removeAt(stack.lastIndex)
                            a + b
                        }
                        "-" -> {
                            if (stack.size == 1) {
                                -stack.removeAt(stack.lastIndex) // Unary negation
                            } else {
                                val b = stack.removeAt(stack.lastIndex)
                                val a = stack.removeAt(stack.lastIndex)
                                a - b
                            }
                        }
                        "*" -> {
                            val b = stack.removeAt(stack.lastIndex)
                            val a = stack.removeAt(stack.lastIndex)
                            a * b
                        }
                        "/" -> {
                            val b = stack.removeAt(stack.lastIndex)
                            val a = stack.removeAt(stack.lastIndex)
                            if (b == 0.0) throw ArithmeticException("Division by zero")
                            a / b
                        }
                        "^" -> {
                            val b = stack.removeAt(stack.lastIndex)
                            val a = stack.removeAt(stack.lastIndex)
                            Math.pow(a, b)
                        }
                        "**" -> {
                            val b = stack.removeAt(stack.lastIndex)
                            val a = stack.removeAt(stack.lastIndex)
                            Math.pow(a, b)
                        }
                        "%" -> {
                            val b = stack.removeAt(stack.lastIndex)
                            val a = stack.removeAt(stack.lastIndex)
                            if (b == 0.0) throw ArithmeticException("Modulo by zero")
                            a % b
                        }
                        else -> throw IllegalArgumentException("Unknown operator: ${token.value}")
                    }
                    stack.add(result)
                }

                TokenType.FUNCTION -> {
                    if (stack.isEmpty()) {
                        throw IllegalArgumentException("Function ${token.value} requires an argument")
                    }

                    val arg = stack.removeAt(stack.lastIndex)
                    val result = when {
                        // Check for custom functions first
                        customFunctions.containsKey(token.value) -> {
                            try {
                                customFunctions[token.value]!!.invoke(arg)
                            } catch (e: Exception) {
                                throw ArithmeticException("Error in custom function '${token.value}': ${e.message}")
                            }
                        }

                        // Built-in functions
                        token.value == "sin" -> Math.sin(arg)
                        token.value == "cos" -> Math.cos(arg)
                        token.value == "tan" -> {
                            // Check if close to pi/2 + k*pi
                            val normalized = arg % Math.PI
                            if (Math.abs(normalized - Math.PI/2) < 1e-10) {
                                throw ArithmeticException("Tangent undefined at pi/2 + k*pi")
                            }
                            Math.tan(arg)
                        }
                        token.value == "asin" -> {
                            if (arg < -1 || arg > 1) {
                                throw ArithmeticException("Argument of asin outside range [-1,1]")
                            }
                            Math.asin(arg)
                        }
                        token.value == "acos" -> {
                            if (arg < -1 || arg > 1) {
                                throw ArithmeticException("Argument of acos outside range [-1,1]")
                            }
                            Math.acos(arg)
                        }
                        token.value == "atan" -> Math.atan(arg)
                        token.value == "log" -> {
                            if (arg <= 0) {
                                throw ArithmeticException("Logarithm of a negative or zero number")
                            }
                            Math.log10(arg)
                        }
                        token.value == "ln" -> {
                            if (arg <= 0) {
                                throw ArithmeticException("Natural logarithm of a negative or zero number")
                            }
                            ln(arg)
                        }
                        token.value == "sqrt" -> {
                            if (arg < 0) {
                                throw ArithmeticException("Square root of a negative number")
                            }
                            Math.sqrt(arg)
                        }
                        token.value == "exp" -> Math.exp(arg)
                        token.value == "abs" -> Math.abs(arg)
                        token.value == "ceil" -> Math.ceil(arg)
                        token.value == "floor" -> Math.floor(arg)
                        else -> throw IllegalArgumentException("Unknown function: ${token.value}")
                    }
                    stack.add(result)
                }

                else -> { /* Ignore other types */ }
            }
        }

        if (stack.size != 1) {
            throw IllegalArgumentException("Invalid expression: incorrect evaluation")
        }

        return stack[0]
    }

    /**
     * Determines if the first operator has higher precedence than the second.
     * 
     * This method implements the rules for operator precedence and associativity:
     * - Higher precedence operators are evaluated before lower precedence ones
     * - For operators with the same precedence, left associativity is used (except for power)
     * - Power operators (^ and **) are right associative
     *
     * @param op1 The first operator token
     * @param op2 The second operator token
     * @return true if op1 has higher precedence than op2, false otherwise
     */
    private fun hasHigherPrecedence(op1: Token, op2: Token): Boolean {
        if (op1.type != TokenType.OPERATOR) return false

        val precedence = mapOf(
            "+" to 1,
            "-" to 1,
            "*" to 2,
            "x" to 2,
            "/" to 2,
            "%" to 2,
            "^" to 3,
            "**" to 3
        )

        val p1 = precedence[op1.value] ?: 0
        val p2 = precedence[op2.value] ?: 0

        // For operators with the same precedence (except power), use left associativity
        // For power operators, use right associativity
        return if (op2.value in listOf("^", "**")) {
            p1 > p2
        } else {
            p1 >= p2
        }
    }
}
