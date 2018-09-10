package vitalyros.diceformula.syntax

import vitalyros.diceformula.parser.*

class SyntaxImpl : Syntax {
    override fun build(e: Expression): Operation {
        return when(e) {
            is DiceLiteralExpr -> RollDiceOp(e.sides)
            is IntLiteralExpr -> UseIntOp(e.value)

            is SumExpr -> buildSum(e)
            is NegExpr -> buildNeg(e)
            is FunExpr -> buildFun(e)

            is MultExpr -> buildMult(e)
            is BracesExpr -> build(e.exp)

            else -> throw SyntaxException("Unexpected expression $e")
        }
    }

    private fun buildFun(e : FunExpr) : Operation {
        val op = build(e.exp)
        return when (e.name) {
            "sum" -> SumFunOp(checkFunTypeArray(e, op))
            "max" -> MaxFunOp(checkFunTypeArray(e, op))
            "min" -> MinFunOp(checkFunTypeArray(e, op))
            "any" -> AnyFunOp(checkFunTypeArray(e, op))
            else -> throw SyntaxException("Unexpected function $e")
        }
    }

    private fun checkFunTypeArray(e : FunExpr, op: Operation) : ArrayOp {
        if (op !is ArrayOp) {
            throw SyntaxException("Expected the nested expression for function ${e.name} to return array.")
        } else {
            return op
        }
    }

    private fun buildMult(e : MultExpr) : Operation {
        val op = build(e.exp)
        return when (op) {
            is DiceOp -> PerformTimesOp(e.multiplier, op)
            is IntOp -> MultByIntOp(e.multiplier, op)
            is ArrayOp -> throw SyntaxException("Right side of a multiplication expected to return integer. ${e.exp}")
            else ->  throw SyntaxException("Unknown type of the left side operation for multiplication ${e.exp}")
        }
    }

    private fun buildSum(e : SumExpr) : Operation {
        val op1 = build(e.exp1)
        val op2 = build(e.exp2)
        if (op1 !is IntOp) {
            throw SyntaxException("Left side of a sum expected to return integer. ${e.exp1}")
        }
        if (op2 !is IntOp) {
            throw SyntaxException("Right side of a sum expected to return integer. ${e.exp2}")
        }
        return if (op1 is DiceOp || op2 is DiceOp) {
            DiceSumOp(op1, op2)
        } else {
            IntSumOp(op1, op2)
        }
    }

    private fun buildNeg(e: NegExpr) : Operation {
        val op = build(e.exp)
        if (op !is IntOp) {
            throw SyntaxException("Right side of a dif expected to return integer. ${e.exp}")
        } else {
            return NegateIntOp(op)
        }
    }
}