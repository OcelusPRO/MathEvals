package fr.ftnl.libs.mathEval.tokenizer


/**
 * Tokenizer spécialisé pour les expressions mathématiques
 */
class MathExpressionTokenizer {
    
    private val tokenRules = listOf(
        // Nombres (priorité haute pour éviter confusion avec variables)
        TokenRule(Regex("""\d+\.\d+[eE][+-]?\d+"""), TokenType.NUMBER, 10), // 2.5e-3
        TokenRule(Regex("""\d+[eE][+-]?\d+"""), TokenType.NUMBER, 9),        // 2e3
        TokenRule(Regex("""\d+\.\d+"""), TokenType.NUMBER, 8),               // 3.14
        TokenRule(Regex("""\d+"""), TokenType.NUMBER, 7),                    // 123
        
        // Fonctions mathématiques (priorité élevée avant variables)
        TokenRule(Regex("""sin|cos|tan|asin|acos|atan"""), TokenType.FUNCTION, 6),
        TokenRule(Regex("""log|ln|exp|sqrt|abs|ceil|floor"""), TokenType.FUNCTION, 6),
        
        // Constantes mathématiques
        TokenRule(Regex("""pi|e"""), TokenType.CONSTANT, 5),
        
        // Opérateurs (priorité selon longueur)
        TokenRule(Regex("""\*\*"""), TokenType.OPERATOR, 4), // ** (puissance alternative)
        TokenRule(Regex("""[+\-*/^%]"""), TokenType.OPERATOR, 3),
        
        // Parenthèses et séparateurs
        TokenRule(Regex("""\("""), TokenType.LPAREN, 2),
        TokenRule(Regex("""\)"""), TokenType.RPAREN, 2),
        TokenRule(Regex(""","""), TokenType.COMMA, 2),
        
        // Variables (priorité la plus basse)
        TokenRule(Regex("""[a-zA-Z][a-zA-Z0-9]*"""), TokenType.VARIABLE, 1)
    )
    
    @Throws(TokenizationException::class)
    fun tokenize(input: String): List<Token> {
        val cleanInput = input.replace(Regex("""\s+"""), "") // Remove spaces
        val initialTokens = mutableListOf<Token>()
        var position = 0
        
        while (position < cleanInput.length) {
            val token = findLongestMatch(cleanInput, position)
                ?: throw TokenizationException(
                    "Invalid character '${cleanInput[position]}'",
                    position,
                    cleanInput[position]
                )
            
            initialTokens.add(token)
            position += token.value.length
        }
        
        // Post-process tokens to handle implicit multiplication
        return initialTokens
    }
    
    private fun findLongestMatch(input: String, startPosition: Int): Token? {
        var longestMatch: Token? = null
        var maxLength = 0
        
        for (rule in tokenRules.sortedByDescending { it.priority }) {
            val remainingInput = input.substring(startPosition)
            val matchResult = rule.pattern.find(remainingInput)
            
            if (matchResult != null && matchResult.range.first == 0) {
                val matchLength = matchResult.value.length
                
                // Principe Maximal Munch : prendre la correspondance la plus longue
                if (matchLength > maxLength) {
                    maxLength = matchLength
                    longestMatch = Token(
                        type = rule.tokenType,
                        value = matchResult.value,
                        position = startPosition
                    )
                }
            }
        }
        
        return longestMatch
    }
}
