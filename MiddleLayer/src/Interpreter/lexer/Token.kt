package interpreter.lexer

interface Token
enum class SymbolTokens: Token {
    LTE, SUM, UNDERSCORE, POWER, LCURL, RCURL,
    MINUS, PLUS, ST, SUBSET, SUBSETEQ, IN, EQUAL
}

data class INTEGER(val str:String): Token
data class VARIABLE(val str:String): Token
