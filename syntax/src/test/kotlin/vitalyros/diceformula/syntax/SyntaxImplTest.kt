package vitalyros.diceformula.syntax

import org.junit.Test
import org.junit.Assert.*
import vitalyros.diceformula.parser.*

class SyntaxImplTest {
    val syntax = SyntaxImpl()

    @Test
    fun testIntLiteral() {
        assertEquals(UseIntOp(10),
                syntax.build(IntLiteralExpr(10)))
    }

    @Test
    fun testMultInt() {
        assertEquals(MultByIntOp(2, MultByIntOp(15, UseIntOp(10))),
                syntax.build(MultExpr(2, MultExpr(15, IntLiteralExpr(10)))))
    }

    @Test
    fun testIntArithmetic() {
        assertEquals(
                IntSumOp(
                        MultByIntOp(1, IntSumOp(UseIntOp(2), UseIntOp(3))),
                        NegateIntOp(MultByIntOp(4, IntSumOp(
                                MultByIntOp(5, IntSumOp(UseIntOp(6), UseIntOp(7))),
                                MultByIntOp(8, MultByIntOp(9, IntSumOp(UseIntOp(10), NegateIntOp(UseIntOp(11)))))
                        ))))
                ,
                syntax.build(
                        //  1 * (2 + 3) - 4 * ( 5 * (6 + 7) + 8 * 9 * (10 - 11))
                        SumExpr(
                                //  1 * (2 + 3)
                                MultExpr(1, BracesExpr(SumExpr(IntLiteralExpr(2), IntLiteralExpr(3)))),
                                //  4 * ( 5 * (6 + 7) + 8 * 9 * (10 - 11))
                                NegExpr(MultExpr(4, BracesExpr(SumExpr(
                                        // 5 * (6 + 7)
                                        MultExpr(5, BracesExpr(SumExpr(IntLiteralExpr(6), IntLiteralExpr(7)))),
                                        // 8 * 9 * (10 - 11)
                                        MultExpr(8, MultExpr(9, BracesExpr(SumExpr(IntLiteralExpr(10), NegExpr(IntLiteralExpr(11))))))
                                ))))
                        )
                ))
    }

    @Test
    fun testDiceLiteral() {
        assertEquals(RollDiceOp(20),
                syntax.build(DiceLiteralExpr(20)))
    }

    @Test
    fun rollSeveralDice() {
        assertEquals(PerformTimesOp(10, RollDiceOp(20)),
                syntax.build(MultExpr(10, DiceLiteralExpr(20))))
    }

    @Test
    fun rollAndAddMultipleTimes() {
        assertEquals(PerformTimesOp(3, DiceSumOp(RollDiceOp(20), UseIntOp(5))),
                syntax.build(MultExpr(3, SumExpr(DiceLiteralExpr(20), IntLiteralExpr(5)))))
    }

    @Test
    fun rollAndAddMultipleTimesThenChooseMax() {
        assertEquals(MaxFunOp(PerformTimesOp(3, DiceSumOp(RollDiceOp(20), UseIntOp(5)))),
                syntax.build(FunExpr("max", MultExpr(3, SumExpr(DiceLiteralExpr(20), IntLiteralExpr(5))))))
    }

    @Test
    fun minFun() {
        assertEquals(MinFunOp(PerformTimesOp(3, RollDiceOp(20))),
                syntax.build(FunExpr("min", MultExpr(3, DiceLiteralExpr(20)))))
    }

    @Test
    fun anyFun() {
        assertEquals(AnyFunOp(PerformTimesOp(3, RollDiceOp(20))),
                syntax.build(FunExpr("any", MultExpr(3, DiceLiteralExpr(20)))))
    }

    @Test
    fun sumFun() {
        assertEquals(SumFunOp(PerformTimesOp(3, RollDiceOp(20))),
                syntax.build(FunExpr("sum", MultExpr(3, DiceLiteralExpr(20)))))
    }
}