package vitalyros.diceformula.parser

import org.junit.Test
import vitalyros.diceformula.lexer.Token
import vitalyros.diceformula.lexer.TokenType
import org.junit.Assert.*

class ParserImplTest {
    @Test
    fun testIntLiteral() {
        val result = parse(listOf(Token(TokenType.INT, 0, "15")))
        assertEquals(IntLiteralExpr(15), result)
    }

    @Test
    fun testDiceLiteral() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20")))
        assertEquals(DiceLiteralExpr(20), result)
    }

    @Test
    fun testSum() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.PLUS, 1, "+"),
                Token(TokenType.INT, 2, "5")))
        assertEquals(SumExpr(DiceLiteralExpr(20), IntLiteralExpr(5)), result)
    }


    @Test
    fun testSum_Chain() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.PLUS, 1, "+"),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.PLUS, 3, "+"),
                Token(TokenType.INT, 4, "5")))
        assertEquals(SumExpr(SumExpr(DiceLiteralExpr(20), DiceLiteralExpr(6)), IntLiteralExpr(5)), result)
    }

    @Test
    fun testDif() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.MINUS, 1, "-"),
                Token(TokenType.INT, 2, "5")))
        assertEquals(SumExpr(DiceLiteralExpr(20), NegExpr(IntLiteralExpr(5))), result)
    }

    @Test
    fun testDif_Chain() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.MINUS, 1, "-"),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.MINUS, 3, "-"),
                Token(TokenType.INT, 4, "5")))
        assertEquals(SumExpr(SumExpr(DiceLiteralExpr(20), NegExpr(DiceLiteralExpr(6))), NegExpr(IntLiteralExpr(5))), result)
    }

    @Test
    fun testBraces() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.MINUS, 1, "-"),
                Token(TokenType.OPEN_BRACE, 2, "("),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.MINUS, 3, "-"),
                Token(TokenType.INT, 4, "5"),
                Token(TokenType.CLOSE_BRACE, 2, ")")))
        assertEquals(SumExpr(DiceLiteralExpr(20), NegExpr(BracesExpr(SumExpr(DiceLiteralExpr(6), NegExpr(IntLiteralExpr(5)))))), result)
    }

    @Test
    fun testBraces_Chain() {
        val result = parse(listOf(
                Token(TokenType.OPEN_BRACE, 2, "("),
                Token(TokenType.OPEN_BRACE, 2, "("),
                Token(TokenType.INT, 4, "5"),
                Token(TokenType.CLOSE_BRACE, 2, ")"),
                Token(TokenType.CLOSE_BRACE, 2, ")")))
        assertEquals(BracesExpr(BracesExpr(IntLiteralExpr(5))), result)
    }

    @Test
    fun testFun() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.MINUS, 1, "-"),
                Token(TokenType.FUN_START, 2, "somefun("),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.MINUS, 3, "-"),
                Token(TokenType.INT, 4, "5"),
                Token(TokenType.CLOSE_BRACE, 2, ")")))
        assertEquals(SumExpr(DiceLiteralExpr(20), NegExpr(FunExpr("somefun", SumExpr(DiceLiteralExpr(6), NegExpr(IntLiteralExpr(5)))))), result)
    }

    @Test
    fun testMult1() {
        val result = parse(listOf(
                Token(TokenType.TIMES, 0, "10 * "),
                Token(TokenType.DICE, 0, "d20")))
        assertEquals(MultExpr(10, DiceLiteralExpr(20)), result)
    }

    @Test
    fun testMult2() {
        val result = parse(listOf(
                Token(TokenType.TIMES, 0, "10 * "),
                Token(TokenType.OPEN_BRACE, 2, "("),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.MINUS, 3, "-"),
                Token(TokenType.INT, 4, "5"),
                Token(TokenType.CLOSE_BRACE, 2, ")")))
        assertEquals(MultExpr(10, BracesExpr(SumExpr(DiceLiteralExpr(6), NegExpr(IntLiteralExpr(5))))), result)
    }

    @Test
    fun testMult3() {
        val result = parse(listOf(
                Token(TokenType.TIMES, 0, "10 * "),
                Token(TokenType.INT, 0, "5")))
        assertEquals(MultExpr(10, IntLiteralExpr(5)), result)
    }


    @Test
    fun testMultSugar1() {
        val result = parse(listOf(
                Token(TokenType.INT, 0, "10"),
                Token(TokenType.DICE, 0, "d20")))
        assertEquals(MultExpr(10, DiceLiteralExpr(20)), result)
    }

    @Test
    fun testMultSugar2() {
        val result = parse(listOf(
                Token(TokenType.INT, 0, "10"),
                Token(TokenType.OPEN_BRACE, 2, "("),
                Token(TokenType.INT, 0, "15"),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.MINUS, 3, "-"),
                Token(TokenType.INT, 4, "5"),
                Token(TokenType.CLOSE_BRACE, 2, ")")))
        assertEquals(MultExpr(10, BracesExpr(SumExpr(MultExpr(15, DiceLiteralExpr(6)), NegExpr(IntLiteralExpr(5))))), result)
    }

    @Test
    fun testMultSugar3() {
        val result = parse(listOf(
                Token(TokenType.INT, 0, "10"),
                Token(TokenType.FUN_START, 0, "somefun("),
                Token(TokenType.INT, 0, "15"),
                Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.CLOSE_BRACE, 0, ")")))
        assertEquals(MultExpr(10, FunExpr("somefun", MultExpr(15, DiceLiteralExpr(20)))), result)
    }

    @Test
    fun testMultSugar4() {
        val result = parse(listOf(
                Token(TokenType.TIMES, 0, "20 *"),
                Token(TokenType.INT, 0, "10"),
                Token(TokenType.DICE, 0, "d20")))
        assertEquals(MultExpr(20, MultExpr(10, DiceLiteralExpr(20))), result)
    }
    @Test
    fun testMultChain() {
        val result = parse(listOf(
                Token(TokenType.TIMES, 0, "1 *"),
                Token(TokenType.TIMES, 0, "2 *"),
                Token(TokenType.FUN_START, 0, "somefun("),
                Token(TokenType.TIMES, 0, "3 *"),
                Token(TokenType.TIMES, 0, "4 *"),
                Token(TokenType.INT, 0, "15"),
                Token(TokenType.OPEN_BRACE, 2, "("),
                Token(TokenType.TIMES, 0, "10 *"),
                Token(TokenType.TIMES, 0, "20 *"),
                Token(TokenType.INT, 0, "30"),
                Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.CLOSE_BRACE, 0, ")"),
                Token(TokenType.CLOSE_BRACE, 0, ")")))
        assertEquals(
                MultExpr(1, MultExpr(2,
                        FunExpr("somefun",
                                MultExpr(3, MultExpr(4, MultExpr(15,
                                        BracesExpr(
                                                MultExpr(10, MultExpr(20, MultExpr(30,
                                                        DiceLiteralExpr(20))
                                                ))
                                        )
                                )))
                        )
                ))
                , result)
    }

    @Test
    fun testSumPrecedence() {
        val result = parse(listOf(
                Token(TokenType.INT, 0, "1"),
                Token(TokenType.PLUS, 0, "+"),
                Token(TokenType.INT, 0, "2"),
                Token(TokenType.MINUS, 0, "-"),
                Token(TokenType.INT, 0, "3")))
        assertEquals(SumExpr(SumExpr(IntLiteralExpr(1), IntLiteralExpr(2)), NegExpr(IntLiteralExpr(3))), result)
    }

    @Test
    fun testBracePrecedence() {
        val result = parse(listOf(
                Token(TokenType.INT, 0, "1"),
                Token(TokenType.MINUS, 0, "-"),
                Token(TokenType.OPEN_BRACE, 0, "("),
                Token(TokenType.INT, 0, "2"),
                Token(TokenType.MINUS, 0, "-"),
                Token(TokenType.INT, 0, "3"),
                Token(TokenType.CLOSE_BRACE, 0, ")")))
        assertEquals(SumExpr(IntLiteralExpr(1), NegExpr(BracesExpr(SumExpr(IntLiteralExpr(2), NegExpr(IntLiteralExpr(3)))))), result)
    }

    @Test
    fun testMultDifSumPrecedence_Short() {
        val result = parse(listOf(
                Token(TokenType.TIMES, 0, "2 *"),
                Token(TokenType.INT, 0, "5"),
                Token(TokenType.MINUS, 0, "-"),
                Token(TokenType.TIMES, 0, "4 *"),
                Token(TokenType.DICE, 0, "d6")))
        assertEquals(
                SumExpr(
                        MultExpr(2, IntLiteralExpr(5)),
                        NegExpr(MultExpr(4, DiceLiteralExpr(6)))
                )
                , result)
    }

    @Test
    fun testMultDifSumPrecedence_Chain() {
        val result = parse(listOf(
                Token(TokenType.TIMES, 0, "10 *"),
                Token(TokenType.TIMES, 0, "20 *"),
                Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.PLUS, 0, "+"),
                Token(TokenType.TIMES, 0, "2 *"),
                Token(TokenType.INT, 0, "5"),
                Token(TokenType.MINUS, 0, "-"),
                Token(TokenType.TIMES, 0, "4 *"),
                Token(TokenType.DICE, 0, "d6")))
        assertEquals(
                SumExpr(
                        SumExpr(
                                MultExpr(10, MultExpr(20, DiceLiteralExpr(20))),
                                MultExpr(2, IntLiteralExpr(5))
                        ),
                        NegExpr(MultExpr(4, DiceLiteralExpr(6)))
                ), result)
    }

    @Test(expected = ParserException::class)
    fun testEmpty() {
        val parser = ParserImpl()
        parser.finish()
    }

    fun parse(tokens: List<Token>): Expression {
        val parser = ParserImpl()
        tokens.forEach { parser.push(it) }
        return parser.finish()
    }

}