package fr.ftnl.libs.mathEval.core.tokenizer

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