package Interpreter

import java.util.*
import java.lang.Integer.parseInt
import java.lang.NumberFormatException

interface Token
enum class SymbolTokens: Token {
    LTE, SUM, UNDERSCORE, POWER, LCURL, RCURL,
    MINUS, PLUS, ST, SUBSET, SUBSETEQ, IN, COMMA
}

data class INTEGER(val str:String): Token
data class VARIABLE(val str:String): Token

interface Expr


fun isNumber(str:String):Boolean{
    try {
        parseInt(str)
    }
    catch (e: NumberFormatException){
        return false
    }
    return true
}

fun getToken(token:String): Token {
    if(isNumber(token)){
        return INTEGER(token)
    }
    when(token){
        "<=" -> return SymbolTokens.LTE
        ":" -> return SymbolTokens.ST
        "sum" -> return SymbolTokens.SUM
        "in" -> return SymbolTokens.IN
        "_" -> return SymbolTokens.UNDERSCORE
        "^" -> return SymbolTokens.POWER
        "{" -> return SymbolTokens.LCURL
        "}" -> return SymbolTokens.RCURL
        else -> return VARIABLE(token)
    }
}


fun Tokenize(input:Sequence<MatchResult>):List<Token>{
    return input.fold(emptyList()){ acc, e -> acc + (getToken(e.value))}
}


class Interpreter {
    val OFgrammar = ObjectiveFunctionGrammar()
    val CSgrammar = ConstraintGrammar()
    val DSgrammar = DatasetGrammar()


}