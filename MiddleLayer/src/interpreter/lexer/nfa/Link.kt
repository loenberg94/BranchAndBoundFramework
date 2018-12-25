package interpreter.lexer.nfa

interface Link

data class Value(val c:String, val to: State): Link
class Epsilon: Link