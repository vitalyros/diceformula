package vitalyros.diceformula.parser

import org.junit.Test
import vitalyros.diceformula.lexer.Token
import vitalyros.diceformula.lexer.TokenType
import org.junit.Assert.*

class ParserImplTest {
    @Test
    fun testIntLiteral() {
        val result = parse(listOf(Token(TokenType.INT, 0, "15")))
        assertEquals(IntLiteral(15), result)
    }

    @Test
    fun testDiceLiteral() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20")))
        assertEquals(DiceLiteral(20), result)
    }

    @Test
    fun testSum() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.PLUS, 1, "+"),
                Token(TokenType.INT, 2, "5")))
        assertEquals(Sum(DiceLiteral(20), IntLiteral(5)), result)
    }


    @Test
    fun testSum_Chain() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.PLUS, 1, "+"),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.PLUS, 3, "+"),
                Token(TokenType.INT, 4, "5")))
        assertEquals(Sum(Sum(DiceLiteral(20), DiceLiteral(6)), IntLiteral(5)), result)
    }

    @Test
    fun testDif() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.MINUS, 1, "-"),
                Token(TokenType.INT, 2, "5")))
        assertEquals(Dif(DiceLiteral(20), IntLiteral(5)), result)
    }

    @Test
    fun testDif_Chain() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.MINUS, 1, "-"),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.MINUS, 3, "-"),
                Token(TokenType.INT, 4, "5")))
        assertEquals(Dif(Dif(DiceLiteral(20), DiceLiteral(6)), IntLiteral(5)), result)
    }

    @Test
    fun testBraces() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.MINUS, 1, "-"),
                Token(TokenType.OPEN_BRACE, 2,  "("),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.MINUS, 3, "-"),
                Token(TokenType.INT, 4, "5"),
                Token(TokenType.CLOSE_BRACE, 2,  ")")))
        assertEquals(Dif(DiceLiteral(20), Braces(Dif(DiceLiteral(6), IntLiteral(5)))), result)
    }

    @Test
    fun testFun() {
        val result = parse(listOf(Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.MINUS, 1, "-"),
                Token(TokenType.FUN_START, 2,  "somefun("),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.MINUS, 3, "-"),
                Token(TokenType.INT, 4, "5"),
                Token(TokenType.CLOSE_BRACE, 2,  ")")))
        assertEquals(Dif(DiceLiteral(20), Fun("somefun", Dif(DiceLiteral(6), IntLiteral(5)))), result)
    }

    @Test
    fun testMult1() {
        val result = parse(listOf(
                Token(TokenType.MULT, 0, "10 * "),
                Token(TokenType.DICE, 0, "d20")))
        assertEquals(Mult(10, DiceLiteral(20)), result)
    }

    @Test
    fun testMult2() {
        val result = parse(listOf(
                Token(TokenType.MULT, 0, "10 * "),
                Token(TokenType.OPEN_BRACE, 2,  "("),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.MINUS, 3, "-"),
                Token(TokenType.INT, 4, "5"),
                Token(TokenType.CLOSE_BRACE, 2,  ")")))
        assertEquals(Mult(10, Braces(Dif(DiceLiteral(6), IntLiteral(5)))), result)
    }


    @Test
    fun testMultSugar1() {
        val result = parse(listOf(
                Token(TokenType.INT, 0, "10"),
                Token(TokenType.DICE, 0, "d20")))
        assertEquals(Mult(10, DiceLiteral(20)), result)
    }

    @Test
    fun testMultSugar2() {
        val result = parse(listOf(
                Token(TokenType.INT, 0, "10"),
                Token(TokenType.OPEN_BRACE, 2,  "("),
                Token(TokenType.INT, 0, "15"),
                Token(TokenType.DICE, 2, "d6"),
                Token(TokenType.MINUS, 3, "-"),
                Token(TokenType.INT, 4, "5"),
                Token(TokenType.CLOSE_BRACE, 2,  ")")))
        assertEquals(Mult(10, Braces(Dif(Mult(15, DiceLiteral(6)), IntLiteral(5)))), result)
    }

    @Test
    fun testMultSugar3() {
        val result = parse(listOf(
                Token(TokenType.INT, 0, "10"),
                Token(TokenType.FUN_START, 2,  "somefun("),
                Token(TokenType.INT, 0, "15"),
                Token(TokenType.DICE, 0, "d20"),
                Token(TokenType.CLOSE_BRACE, 2,  ")")))
        assertEquals(Mult(10, Fun("somefun", Mult(15, DiceLiteral(20)))), result)
    }

    @Test
    fun testMultSugar4() {
        val result = parse(listOf(
                Token(TokenType.MULT, 0, "20 *"),
                Token(TokenType.INT, 1, "10"),
                Token(TokenType.DICE, 0, "d20")))
        assertEquals(Mult(20, Mult(10, DiceLiteral(20))), result)
    }

    @Test(expected = ParserException::class)
    fun testEmpty() {
        val parser = ParserImpl()
        parser.finish()
    }

    fun parse(tokens: List<Token>) : Expression {
        val parser = ParserImpl()
        tokens.forEach { parser.push(it) }
        return parser.finish()
    }

}