package interpreter

class ConstraintGrammar:Grammar(){
    val sum by token("sum")
    val underscore by token("_")
    val lcurl by token("\\{")
    val rcurl by token("}")
    val equal by token("=")
    val coef  by token("([a-z]_[a-z]+)")
    val number by token("[0-9]+")
    val variable by token("[^\\w]*(\\w)[^\\w]")
    val plus by token("\\+")
    val minus by token("-")
    val power by token("\\^")
    val inSign by token("in")
    val digit by token("[0-9]+")
    val lte by token("<=")
    val loop by token("for")
    val range by token("\\.\\.\\.")
    val ws by token("\\s+")
}