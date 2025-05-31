package fr.ftnl.libs.mathEval.tokenizer

/**
 * Types de tokens dans les expressions mathématiques
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