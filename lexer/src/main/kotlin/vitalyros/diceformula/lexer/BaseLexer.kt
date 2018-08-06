package vitalyros.diceformula.lexer

import vitalyros.diceformula.parser.Parser

abstract class BaseLexer(val parser: Parser): Lexer {
    protected fun emit(data: ByteArray, ts: Int, te: Int, type: TokenType) {
        parser.push(Token(type, ts, fetchString(data, ts, te)))
    }

    private fun copy(data: ByteArray, ts: Int, te: Int): ByteArray {
        val result = ByteArray(te - ts)
        System.arraycopy(data, ts, result, 0, te - ts)
        return result
    }

    private fun fetchString(data: ByteArray, ts: Int, te: Int): String {
        return String(copy(data, ts, te))
    }
}