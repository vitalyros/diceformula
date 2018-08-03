import org.junit.Assert.*
import vitalyros.diceformula.lexer.Lexer
import org.junit.Test
import vitalyros.diceformula.common.Parser
import vitalyros.diceformula.common.Token
import vitalyros.diceformula.common.TokenType

class LexerTest {
    @Test
    fun testDice() = testLexer("d6",
            listOf(Token(TokenType.DICE, 0, "d6")))

    @Test
    fun testMultiDice1() = testLexer("20d6",
            listOf(Token(TokenType.INT, 0, "20"),
                    Token(TokenType.DICE, 2, "d6")))

    @Test
    fun testMultiDice2() = testLexer("20 * d6",
            listOf(Token(TokenType.TIMES, 0, "20 *"),
                    Token(TokenType.DICE, 5, "d6")))


    @Test
    fun testMultiDice3() = testLexer("20(d6 + 5)",
            listOf(Token(TokenType.INT, 0, "20"),
                    Token(TokenType.OPEN_BRACE, 2, "("),
                    Token(TokenType.DICE, 3, "d6"),
                    Token(TokenType.PLUS, 6, "+"),
                    Token(TokenType.INT, 8, "5"),
                    Token(TokenType.CLOSE_BRACE, 9, ")")))

    @Test
    fun testMultiDice4() = testLexer("20 * (d6 + 5)",
            listOf(Token(TokenType.TIMES, 0, "20 *"),
                    Token(TokenType.OPEN_BRACE, 5, "("),
                    Token(TokenType.DICE, 6, "d6"),
                    Token(TokenType.PLUS, 9, "+"),
                    Token(TokenType.INT, 11, "5"),
                    Token(TokenType.CLOSE_BRACE, 12, ")")))

    @Test
    fun testPlus1() = testLexer("15 + 5",
            listOf(Token(TokenType.INT, 0, "15"),
                    Token(TokenType.PLUS, 3, "+"),
                    Token(TokenType.INT, 5, "5")))

    @Test
    fun testPlus2() = testLexer("d20 + d6",
            listOf(Token(TokenType.DICE, 0, "d20"),
                    Token(TokenType.PLUS, 4, "+"),
                    Token(TokenType.DICE, 6, "d6")))

    @Test
    fun testPlus3() = testLexer("10+ d20 +d6+5  +   7",
            listOf(Token(TokenType.INT, 0, "10"),
                    Token(TokenType.PLUS, 2, "+"),
                    Token(TokenType.DICE, 4, "d20"),
                    Token(TokenType.PLUS, 8, "+"),
                    Token(TokenType.DICE, 9, "d6"),
                    Token(TokenType.PLUS, 11, "+"),
                    Token(TokenType.INT, 12, "5"),
                    Token(TokenType.PLUS, 15, "+"),
                    Token(TokenType.INT, 19, "7")))

    @Test
    fun testMinus1() = testLexer("15 - 5",
            listOf(Token(TokenType.INT, 0, "15"),
                    Token(TokenType.MINUS, 3, "-"),
                    Token(TokenType.INT, 5, "5")))

    @Test
    fun testMinus2() = testLexer("d20 - d6",
            listOf(Token(TokenType.DICE, 0, "d20"),
                    Token(TokenType.MINUS, 4, "-"),
                    Token(TokenType.DICE, 6, "d6")))

    @Test
    fun testMinus3() = testLexer("10- d20 -d6-5  -   7",
            listOf(Token(TokenType.INT, 0, "10"),
                    Token(TokenType.MINUS, 2, "-"),
                    Token(TokenType.DICE, 4, "d20"),
                    Token(TokenType.MINUS, 8, "-"),
                    Token(TokenType.DICE, 9, "d6"),
                    Token(TokenType.MINUS, 11, "-"),
                    Token(TokenType.INT, 12, "5"),
                    Token(TokenType.MINUS, 15, "-"),
                    Token(TokenType.INT, 19, "7")))

    @Test
    fun testFun1() = testLexer("max(2d20 + 5) - min  (  2d6  )",
            listOf(Token(TokenType.FUN_START, 0, "max("),
                    Token(TokenType.INT, 4, "2"),
                    Token(TokenType.DICE, 5, "d20"),
                    Token(TokenType.PLUS, 9, "+"),
                    Token(TokenType.INT, 11, "5"),
                    Token(TokenType.CLOSE_BRACE, 12, ")"),
                    Token(TokenType.MINUS, 14, "-"),
                    Token(TokenType.FUN_START, 16, "min  ("),
                    Token(TokenType.INT, 24, "2"),
                    Token(TokenType.DICE, 25, "d6"),
                    Token(TokenType.CLOSE_BRACE, 29, ")")))

    fun testLexer(text: String, expected: List<Token>) {
        val parser = ListParser()
        val lexer = Lexer(parser)
        lexer.runLexer(text.toByteArray(Charsets.UTF_8))
        assertEquals(expected, parser.tokens)
    }
}

class ListParser(val tokens: ArrayList<Token> = ArrayList()) : Parser {
    override fun push(token: Token) {
        tokens.add(token)
    }
}