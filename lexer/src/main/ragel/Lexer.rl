// -*- coding: utf-8 -*-
%%{

machine simple_lexer;

action emit_open_brace { emitOpenBrace(data, tokens, ts, te); }
action emit_close_brace { emitCloseBrace(data, tokens, ts, te); }
action emit_fun_start { emitFunStart(data, tokens, ts, te); }
action emit_dice { emitDice(data, tokens, ts, te); }
action emit_plus { emitPlus(data, tokens, ts, te); }
action emit_minus { emitMinus(data, tokens, ts, te); }
action emit_int { emitInt(data, tokens, ts, te); }
action emit_times { emitTimes(data, tokens, ts, te); }

int = [1-9][0-9]*;

dice = 'd' int;

times = int space '*' space;

plus = space '+' space;
minus = space '-' space;

open_brace = '(';
close_brace = ')';

fun_name = [a-z]+;
fun_start = fun_name open_brace;

main := |*
  space;
  plus => emit_plus;
  int => emit_int;
    times => emit_times;

  minus => emit_minus;
  fun_start => emit_fun_start;
  close_brace => emit_close_brace;
  open_brace => emit_open_brace;
  dice => emit_dice;
  space;
*|;

}%%

package vitalyros.diceformula.lexer;

import java.util.*;

public class Lexer {
   public void emitInt(byte[] data, List<String> tokens, int ts, int te) {
        String str = fetchString(data, ts,  te);
        System.out.println("emitInt: " + str);
   }

   public void emitTimes(byte[] data, List<String> tokens, int ts, int te) {
        String str = fetchString(data, ts,  te);
        System.out.println("emitTimes: " + str);
   }

   public void emitPlus(byte[] data, List<String> tokens, int ts, int te) {
        String str = fetchString(data, ts,  te);
        System.out.println("emitPlus: " + str);
   }
  public void emitMinus(byte[] data, List<String> tokens, int ts, int te) {
       String str = fetchString(data, ts,  te);
       System.out.println("emitMinus: " + str);
  }

   public void emitFunStart(byte[] data, List<String> tokens, int ts, int te) {
        String str = fetchString(data, ts,  te);
        System.out.println("emitFunStart: " + str);
   }

   public void emitOpenBrace(byte[] data, List<String> tokens, int ts, int te) {
        String str = fetchString(data, ts,  te);
        System.out.println("emitOpenBrace: " + str);
   }

   public void emitCloseBrace(byte[] data, List<String> tokens, int ts, int te) {
        String str = fetchString(data, ts,  te);
        System.out.println("emitCloseBrace: " + str);
   }

   public void emitManyDice(byte[] data, List<String> tokens, int ts, int te) {
        String str = fetchString(data, ts,  te);
        System.out.println("ManyDice: " + str);
   }

    public void emitDice(byte[] data, List<String> tokens, int ts, int te) {
        String str = fetchString(data, ts,  te);
        String numString = str.substring(1, str.length());
        Dice dice = new Dice(Integer.parseInt(numString));
        System.out.println("Dice: " + dice);
    }

    public void emit(String token, byte[] data, List<String> tokens, int ts, int te) {
         byte[] output = new byte[te - ts];
         System.arraycopy(data, ts, output, 0, te - ts);
         System.out.println(token + " - " + new String(output) + " "  + output.length);
         tokens.add(new String(output));
    }

    public byte[] copy(byte[] data, int ts, int te) {
        byte[] result = new byte[te - ts];
        System.arraycopy(data, ts, result, 0, te - ts);
        return result;
    }

    public String fetchString(byte[] data, int ts, int te) {
        return new String(copy(data, ts, te));
    }
    %% write data;

    public void runLexer(byte[] data) {
        int eof = data.length;
        int p = 0, pe = data.length, te, ts, cs, act;
        List<String> tokens = new ArrayList<String>();

        %% write init;
        %% write exec;

        System.out.println(tokens);
    }
}