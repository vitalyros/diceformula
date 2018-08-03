// -*- coding: utf-8 -*-
%%{

machine simple_lexer;

int = [1-9][0-9]*;

dice = 'd' int;

times = int space* '*';

plus = '+';
minus = '-';

open_brace = '(';
close_brace = ')';

fun_name = [a-z]+;
fun_start = fun_name space* open_brace;

main := |*
  plus => { emit(data, ts, te, TokenType.PLUS); };
  minus => { emit(data, ts, te, TokenType.MINUS); };

  int => { emit(data, ts, te, TokenType.INT); };
  times => { emit(data, ts, te, TokenType.TIMES); };

  fun_start => { emit(data, ts, te, TokenType.FUN_START); };
  open_brace => { emit(data, ts, te, TokenType.OPEN_BRACE); };
  close_brace => { emit(data, ts, te, TokenType.CLOSE_BRACE); };

  dice => { emit(data, ts, te, TokenType.DICE); };
  space;
*|;

}%%

package vitalyros.diceformula.lexer;

import vitalyros.diceformula.common.Parser;
import vitalyros.diceformula.common.TokenType;
import java.util.*;

public class Lexer extends BaseLexer {
    public Lexer(Parser parser) {
        super(parser);
    }

    %% write data;

    public void runLexer(byte[] data) {
        int eof = data.length;
        int p = 0, pe = data.length, te, ts, cs, act;
        %% write init;
        %% write exec;
    }
}