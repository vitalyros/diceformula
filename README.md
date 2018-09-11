# Dice formula

Expression language dedicated to rolling dice. 
Intended to calculate expressions, often used in tabletop games.

## Examples
- d20 : roll a 20-sided dice and return the outcome.
- d20 + d6 - 5 : roll a 20-sided dice, then a 6-sided dice. Sum the outcomes of the dice rolls, then subtract 5.
- max(3d20) + 5 : roll three 20-sided dice, select the greatest outcome, then add 5.
- sum(10(d4+3)) : ten times roll a 4-sided dice and add 3. Return the sum of those values.
- sum(10d4) + 30 : roll ten 4-sided dice, sum the values, then add 30. Arithmetically equivalent to the sum(10(d4+3)).
- 10(d10 + 2) : ten times roll a 10-sided dice and add 2. Return the array of the outcomes of those rolls.

## Build
run `./gradlew build`

## Running

### Kotlin
```
import vitalyros.diceformula.core.DiceFormula

fun main(vararg s: String) {
    DiceFormula.exec("d20 + 5")
}    
```


## Syntax

Two types of literals can be used in the expressions.
- integers - e.g. 5 20 60
- dice rolls - e.g. d20 d4 - those would cause a dice roll

Addition, multiplication and subtraction operations should work with integers. Operator precedence should work by the rules of arithmetic.
e.g. `20 * 5 + 4 - 2` would return 102

To control precedence braces can be used.
e.g. `20 * (20 - 4)`

Addition and subtraction should work similarly with dice rolls. Integers and dice rolls can be mixed.
e.g. `d20 - d6 + 5` : in case d20 rolls a 10 and d6 rolls a 4 - the expression should return 9.

Multiplication operator `*` works differently based on context:
- If the right side of a multiplication operator is an arithmetic expression: 
The operator means arithmetic multiplication.
e.g. `10 * (20 - 4)` would simply return 160
- If the right side of a multiplication operator calls for a dice roll: The operator means repeat X times operation. 
The left side of a multiplication operator would be executed multiple times and the results would be accumulated into an array of integers.
e.g. `10 * (d6 + 5)` would return an array of size 10 and each of the elements would be results of `d6 + 5` expression executions

The left side of a multiplication operator must be an integer, and the right side may be a complex expression.
e.g. 
expressions `50 * (d4 + 5)` and `50 * (10 + 5)` are ok but 
expressions `(d4 + 5) * 50` or `(10 + 5) * 50` are not ok.

It is ok to miss the asterisk `*` sign if the left side is a dice roll or a braced expression.
e.g. `10d4` or `10(d4+5)` are valid expressions that include repeat 10 times operation
and `10(4*5)` is a valid expression that includes arithmetic multiplication by 10.

During runtime the language operates with two data types. 
- integer
- array of integers
Either of those may be a result of the execution.

Simple arithmetic operations can't be performed on the array of integers. 
e.g. `20d4 + 10` is not ok because `20d4` is a repeat operation that will return an array of 20 values and you can't add an integer to that.
By the same reason repeat operations can't be nested.
e.g. `20 * 20 * (d4 + 5)` is not ok.

But there are special operations that can process arrays. 
- max - select and return the greatest value.
- min - select and return the lowest value.
- sum - return the sum of values.
- any - return any single value.
The syntax is akin to C language function call.
e.g. `max(10d20)` would execute 10 20-sided dice rolls and return the greatest of them.



