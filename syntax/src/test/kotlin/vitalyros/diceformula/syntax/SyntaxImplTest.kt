package vitalyros.diceformula.syntax

import org.junit.Test
import org.junit.Assert.*
import vitalyros.diceformula.parser.*

class SyntaxImplTest {
    val syntax = SyntaxImpl()

    @Test
    fun testIntLiteral() {
        assertEquals(UseInt(10),
                syntax.build(IntLiteral(10)))
    }

    @Test
    fun testMultInt() {
        assertEquals(MultByInt(2, MultByInt(15, UseInt(10))),
                syntax.build(Mult(2, Mult(15, IntLiteral(10)))))
    }

    @Test
    fun testIntArithmetic() {
        assertEquals(
                IntSum(
                        MultByInt(1, IntSum(UseInt(2), UseInt(3))),
                        UseNegativeInt(MultByInt(4, IntSum(
                                MultByInt(5, IntSum(UseInt(6), UseInt(7))),
                                MultByInt(8, MultByInt(9, IntSum(UseInt(10), UseNegativeInt(UseInt(11)))))
                        ))))
                ,
                syntax.build(
                        //  1 * (2 + 3) - 4 * ( 5 * (6 + 7) + 8 * 9 * (10 - 11))
                        Dif(
                                //  1 * (2 + 3)
                                Mult(1, Braces(Sum(IntLiteral(2), IntLiteral(3)))),
                                //  4 * ( 5 * (6 + 7) + 8 * 9 * (10 - 11))
                                Mult(4, Braces(Sum(
                                        // 5 * (6 + 7)
                                        Mult(5, Braces(Sum(IntLiteral(6), IntLiteral(7)))),
                                        // 8 * 9 * (10 - 11)
                                        Mult(8, Mult(9, Braces(Dif(IntLiteral(10), IntLiteral(11)))))
                                )))
                        )
                ))
    }
    
    @Test
    fun testDiceLiteral() {
        assertEquals(RollDice(20),
                syntax.build(DiceLiteral(20)))
    }

    @Test
    fun rollSeveralDice() {
        assertEquals(PerformTimes(10, RollDice(20)),
                syntax.build(Mult(10, DiceLiteral(20))))
    }

    @Test
    fun rollAndAddMultipleTimes() {
        assertEquals(PerformTimes(3, DiceSum(RollDice(20), UseInt(5))),
                syntax.build(Mult(3, Sum(DiceLiteral(20), IntLiteral(5)))))
    }

    @Test
    fun rollAndAddMultipleTimesThenChooseMax() {
        assertEquals(MaxFun(PerformTimes(3, DiceSum(RollDice(20), UseInt(5)))),
                syntax.build(Fun("max", Mult(3, Sum(DiceLiteral(20), IntLiteral(5))))))
    }

    @Test
    fun minFun() {
        assertEquals(MinFun(PerformTimes(3, RollDice(20))),
                syntax.build(Fun("min", Mult(3, DiceLiteral(20)))))
    }

    @Test
    fun anyFun() {
        assertEquals(AnyFun(PerformTimes(3, RollDice(20))),
                syntax.build(Fun("any", Mult(3, DiceLiteral(20)))))
    }

    @Test
    fun sumFun() {
        assertEquals(SumFun(PerformTimes(3, RollDice(20))),
                syntax.build(Fun("sum", Mult(3, DiceLiteral(20)))))
    }
}