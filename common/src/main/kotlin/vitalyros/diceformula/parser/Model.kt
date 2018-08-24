package vitalyros.diceformula.parser

interface Expression

data class DiceLiteral(val sides: Int) : Expression
data class IntLiteral(val value: Int) : Expression

data class Neg(val exp: Expression) : Expression
data class Sum(val exp1: Expression, val exp2: Expression) : Expression
data class Fun(val name: String, val exp: Expression) : Expression

data class Mult(val multiplier: Int, val exp: Expression) : Expression
data class Braces(val exp: Expression) : Expression

