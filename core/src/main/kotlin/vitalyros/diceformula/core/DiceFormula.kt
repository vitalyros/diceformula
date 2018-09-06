package vitalyros.diceformula.core

import vitalyros.diceformula.lexer.RagelLexer
import vitalyros.diceformula.parser.ParserImpl
import vitalyros.diceformula.syntax.SyntaxImpl
import vitalyros.diceformula.translator.TranslatorImpl
import vitalyros.diceformula.runtime.*


class DiceFormula {
    companion object {
        @JvmStatic
        fun exec(source: String) = exec(source.toByteArray(), SimpleDiceRoller())

        @JvmStatic
        fun exec(source: ByteArray, diceRoller: DiceRoller) : Any {
            val parser = ParserImpl()
            val lexer = RagelLexer(parser)
            val syntax = SyntaxImpl()
            val translator = TranslatorImpl()
            lexer.runLexer(source)
            return JavaSyncRuntime(translator.translate(syntax.build(parser.finish())), diceRoller).exec()
        }
    }

}