package vitalyros.diceformula.parser

interface Expression

data class DiceLiteralExpr(val sides: Int) : Expression
data class IntLiteralExpr(val value: Int) : Expression

data class NegExpr(val exp: Expression) : Expression
data class SumExpr(val exp1: Expression, val exp2: Expression) : Expression
data class FunExpr(val name: String, val exp: Expression) : Expression

data class MultExpr(val multiplier: Int, val exp: Expression) : Expression
data class BracesExpr(val exp: Expression) : Expression
