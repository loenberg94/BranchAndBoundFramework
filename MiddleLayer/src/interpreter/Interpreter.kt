package interpreter

import interpreter.lexer.ConstraintGrammar
import interpreter.lexer.DatasetGrammar
import interpreter.lexer.ObjectiveFunctionGrammar

class Interpreter {
    private val OFgrammar = ObjectiveFunctionGrammar()
    private val CSgrammar = ConstraintGrammar()
    private val DSgrammar = DatasetGrammar()

    fun getDataset(input:String){
        val tokenMatchList = DSgrammar.tokenizer.tokenize(input)

    }
}