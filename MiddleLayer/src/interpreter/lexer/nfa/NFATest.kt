package Lexer.nfa

import org.junit.Assert.*
import org.junit.Test

class NFATest {

    private val strings = arrayOf("a(ab|bc|cd)d",
            "a(b|c)",
            "(a|b)c",
            "a(b|c)d",
            "(a|b)",
            "a|b")


    @Test
    fun parenthesize() {
        val nfa = NFA()
        val expected = arrayOf("(a)((ab)|(bc)|(cd))(d)",
                "(a)((b)|(c))",
                "((a)|(b))(c)",
                "(a)((b)|(c))(d)",
                "((a)|(b))",
                "(a)|(b)")

        for(i in 0 until strings.size){
            val ns = nfa.parenthesize(strings[i])
            assertEquals(expected[i],ns)
        }
    }

    @Test
    fun postfix() {
        val nfa = NFA()
        val expected = arrayOf("(a)((ab)(bc)|(cd)|)(d)",
                "(a)((b)(c)|)",
                "((a)(b)|)(c)",
                "(a)((b)(c)|)(d)",
                "((a)(b)|)",
                "(a)(b)|")

        for(i in 0 until strings.size){
            val ns = nfa.postfix(nfa.parenthesize(strings[i]))
            assertEquals(expected[i],ns)
        }
    }

    @Test
    fun foldout() {
        val nfa = NFA()
        println(nfa.foldout("[abcd0123-]+"))
        //println(nfa.foldout("[0-3a-cA-C]+"))
        //println(List(99 - 97 + 1) { i -> (i + 97).toChar().toString()})
    }

    @Test
    fun createNFA() {
    }

    @Test
    fun main() {

    }
}