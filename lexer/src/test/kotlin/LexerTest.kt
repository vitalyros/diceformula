import vitalyros.diceformula.lexer.Lexer
import org.junit.Test

class LexerTest {
    val lexer = Lexer()

    @Test
    fun testDice() {
        runLexer("d6")
    }

    @Test
    fun testMultiDice1() {
        runLexer("20d6")
    }

    @Test
    fun testMultiDice2() {
        runLexer("20 * d6")
    }

    @Test
    fun testMultiDice3() {
        runLexer("20(d6 + 5)")
    }

    @Test
    fun testMultiDice4() {
        runLexer("20 * (d6 + 5)")
    }

    @Test
    fun testPlus1() {
        runLexer("15 + 5")
    }

    @Test
    fun testPlus2() {
        runLexer("d20 + d6")
    }

    @Test
    fun testPlus3() {
        runLexer("10 + d20 + d6 + 5")
    }

    @Test
    fun testMinus1() {
        runLexer("15 - 5")
    }

    @Test
    fun testMinus2() {
        runLexer("d20 - d6")
    }

    @Test
    fun testMinus3() {
        runLexer("10 - d20 - d6 - 5")
    }

    @Test
    fun testFun1() {
        runLexer("max(d20 + 5)")
    }

    @Test
    fun testComplex1() {
        runLexer("max(2d20) + d6 + 5")
    }

    @Test
    fun testComplex2() {
        runLexer("sum(5d8) + sum(2d10) + 5")
    }

    fun runLexer(text: String) {
        System.out.println(text)
        val res = lexer.runLexer(text.toByteArray(Charsets.UTF_8))
        System.out.println(res)
    }
}