@file:Suppress("PackageName")

package Lexer.nfa

import java.util.*
import kotlin.collections.ArrayList

class NFA {

    var start: State? = null

    fun parenthesize(input:String):String{
        var retStr = StringBuilder().append('(')
        for(i in 0 until input.length){
            val char = input[i]
            when(char){
                '(' -> {
                    if(i != 0) retStr.append(")$char(")
                    else retStr.append(char)
                }
                ')' -> {
                    if(i != input.length - 1) retStr.append(")$char(")
                    else retStr.append(char)
                }
                '|' -> retStr.append(")|(")
                else -> retStr.append(char)
            }
        }
        return "$retStr)"
    }

    fun postfix(input:String):String{
        var retStr = ""
        var operators = Stack<Char>()

        for (c in input){
            when(c){
                ')' -> {
                    retStr += c
                    if(operators.isNotEmpty()) retStr += operators.pop()
                }
                '|' -> operators.push(c)
                else -> retStr += c

            }
        }
        return retStr
    }

    private fun getCharRange(c1:Char, c2:Char):List<String> {
        val s:Int = c1.toInt()
        val e:Int = c2.toInt()
        return List(e - s + 1) { i -> (i + s).toChar().toString()}
    }

    private fun sBrackets(iStart:Int, iEnd:Int, string:String):String{
        var i = iStart
        val sb = ArrayList<String>()
        while(i <= iEnd){
            when (string[i]){
                '-' -> {
                    when (i){
                        iStart -> sb.add("-")
                        iEnd -> sb.add("-")
                        else -> {
                            if(string[i - 1].isLetter() && string[i + 1].isLetter()){
                                if((string[i - 1].isLowerCase() && string[i + 1].isLowerCase()) || (string[i - 1].isUpperCase() && string[i + 1].isUpperCase())){
                                    sb.add(getCharRange(string[i - 1],string[i + 1]).joinToString("|"))
                                    i++
                                }
                                else{
                                    throw Exception("${string[i - 1]}-${string[i + 1]}: needs to be same case")
                                }
                            }
                            else if(string[i - 1].isDigit() && string[i + 1].isDigit()){
                                sb.add(getCharRange(string[i - 1],string[i + 1]).joinToString("|"))
                                i++
                            }
                            else{
                                throw Exception("${string[i - 1]}-${string[i + 1]}: is not compatible")
                            }
                        }
                    }
                }
                else -> {
                    if (string[i].isLetter() || string[i].isDigit()){
                        if(i < iEnd - 1 && i > iStart){
                            if(string[i - 1] != '-' && string[i + 1] != '-'){
                                sb.add("${string[i]}")
                            }
                        }
                        else if((i < iEnd - 1 && string[i + 1] != '-') || (i > iStart && string[i - 1] != '-')){
                            sb.add("${string[i]}")
                        }
                    }
                    else {
                        sb.add("${string[i]}")
                    }
                }
            }
            i++
        }
        return "(${sb.joinToString("|")})"
    }

    fun foldout(input:String):String{
        val stack = Stack<Int>()
        var string = input

        var i = 0
        while (i < string.length){
            if(string[i] == '('){
                stack.push(i)
            }
            else if(string[i] == ')'){
                if (i != string.length - 1 && string[i + 1] != '+' && stack.isNotEmpty()) stack.pop()
            }
            else if(string[i] == '['){
                stack.push(i)
            }
            else if(string[i] == ']'){
                val iS = stack.pop()
                val rString = sBrackets(iS + 1, i -1, string)
                string = string.replaceRange(iS,i + 1,rString)
                i = iS + rString.length - 1
            }
            else if(string[i] == '+'){
                if(string[i - 1] == ')'){
                    var iS = -1
                    var bC = 0
                    var tI = i - 2
                    while (tI >= 0){
                        if(string[tI] == ')') bC++
                        else if (string[tI] == '('){
                            if(bC == 0){
                                iS = tI
                                break
                            }
                            else bC--
                        }
                        tI--
                    }
                    val sString = string.subSequence(iS,i)
                    string = string.replaceRange(iS,i + 1, "$sString$sString*")
                    i = iS + sString.length - 1
                }
                else{
                    var iS = -1
                    var tI = i - 1
                    while(tI >= 0){
                        if(string[tI] == ')'){
                            iS = tI
                            break
                        }
                        else if(string[tI] == ']'){
                            iS = tI
                            break
                        }
                        else if(string[tI] == '|'){
                            iS = tI
                            break
                        }
                        tI--
                    }
                    val sString = string.subSequence(iS + 1, i).toString()
                    string = string.replaceRange(iS + 1,i + 1, "$sString($sString)*" )
                    i = iS + sString.length - 1
                }
            }
            i++
        }
        return string
    }

    fun createNFA(input:String){
        val regexString = postfix(parenthesize(foldout(input)))
        var i = regexString.length - 1
        while (i >= 0){

        }
    }

    constructor(){
        start = State(StateType.START, null, emptyList())
    }
}