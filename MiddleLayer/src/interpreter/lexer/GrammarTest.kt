package interpreter.lexer

import org.junit.Test

class GrammarTest {

    class tGrammar: Grammar() {
        val sumToken by token("sum")
        val ws by token("\\s+")
    }

    private val testGrammer = tGrammar()

    @Test
    fun test(){

    }

}