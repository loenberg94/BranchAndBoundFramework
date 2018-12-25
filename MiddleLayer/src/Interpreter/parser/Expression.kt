package interpreter.parser

interface Expression

data class Integer(val value:Int): Expression
data class Id(val value:String): Expression
data class Index(val id: Id, val indecies: Indecies): Expression

data class Plus(val expr1: Expression, val expr2: Expression): Expression
data class Minus(val expr1: Expression, val expr2: Expression): Expression
data class Times(val expr1: Expression, val expr2: Expression): Expression

data class Sum(val from: Id, val to: Id, val expr: Expression): Expression
data class Lte(val expr1: Expression, val expr2: Expression): Expression
data class Equal(val expr1: Expression, val expr2: Expression): Expression

data class Subset(val expr1: Expression, val expr2: Expression): Expression
data class SubsetEq(val expr1: Expression, val expr2: Expression): Expression
data class In(val indexVar: Id, val setVar: Id): Expression


interface Indecies: Expression
data class single(val id: Id): Indecies
data class double(val id1: Id, val id2: Id): Indecies

