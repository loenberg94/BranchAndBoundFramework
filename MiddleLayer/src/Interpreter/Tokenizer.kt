package Interpreter

import org.intellij.lang.annotations.Language
import java.lang.Exception

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

class Tokenizer(tokens:List<TokenDefinition>) {
    val patterns =
            tokens.map { it to (it.regex ?: it.pattern.toRegex()) }

    val collectedPattern =
            patterns
                    .joinToString("|")
                    .toRegex()

    fun retrieveTokens(input:String):Sequence<MatchResult>{
        val matches = collectedPattern.findAll(input)
        val rem = collectedPattern.replace(input,"").replace(" ", "")
        if (rem.isNotEmpty()){
            throw Exception("Illigal tokens: $rem")
        }
        return matches
    }
}