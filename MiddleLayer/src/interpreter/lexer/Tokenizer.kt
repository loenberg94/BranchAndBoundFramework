package interpreter.lexer

import exceptions.LexicalException
import interpreter.None
import interpreter.Option
import interpreter.Some
import org.intellij.lang.annotations.Language
import java.lang.Integer
import java.lang.NumberFormatException

class TokenDefinition {
    val pattern:String
    val regex:Regex?

    constructor(@Language("RegExp") patternString: String){
        pattern = patternString
        regex = null
    }

    constructor(regex: Regex){
        pattern = regex.pattern
        this.regex = regex
    }
}

data class TokenMatch(val token: Token, val position:Int, val row:Int, val column:Int){
    override fun toString(): String {
        return "$token at $position ($row:$column)"
    }
}

class Tokenizer(tokens:List<TokenDefinition>) {

    private val patterns =
            List(tokens.size) {i -> tokens[i].pattern}


    private val collectedPattern =
            patterns
                    .joinToString("|","(?:",")")
                    .toRegex()


    private fun isNumber(str:String):Boolean{
        try {
            Integer.parseInt(str)
        }
        catch (e: NumberFormatException){
            return false
        }
        return true
    }
    private fun getToken(token:String): Option {
        if(isNumber(token)){
            return Some(INTEGER(token))
        }
        if(token.matches("\\s+".toRegex())) {
            return None()
        }

        return when(token){
            "<=" -> Some(SymbolTokens.LTE)
            ":" -> Some(SymbolTokens.ST)
            "sum" -> Some(SymbolTokens.SUM)
            "in" -> Some(SymbolTokens.IN)
            "_" -> Some(SymbolTokens.UNDERSCORE)
            "^" -> Some(SymbolTokens.POWER)
            "{" -> Some(SymbolTokens.LCURL)
            "}" -> Some(SymbolTokens.RCURL)
            else -> Some(VARIABLE(token))
        }
    }

    fun tokenize(input:String):ArrayList<TokenMatch>{
        var pos = 0
        var col = 1
        var row = 1

        val ftList = ArrayList<TokenMatch>()

        while(pos < input.length){
            val mResult = collectedPattern.find(input,pos)

            if(mResult != null && mResult.range.first == pos){
                val match = mResult.groupValues[0]

                pos += match.length
                col += match.length

                val nRows = match.count { it == '\n' }
                row += nRows
                if(nRows > 0) col = match.length - match.lastIndexOf('\n')

                when (val token = getToken(match)){
                    is Some<*> ->{
                        val result = TokenMatch(token.getVal() as Token, pos, row, col)
                        ftList.add(result)
                    }
                }
            }
            else{
                throw LexicalException(input.substring(pos), pos, row, col)
            }
        }

        return ftList
    }
}