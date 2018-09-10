# Dice formula

Expression language dedicated to rolling dice. 
Intended to calculate expressions, often used in tabletop games.

## Examples
- d20 -> roll a 20-sided dice and return the outcome.
- d20 + d6 - 5 -> roll a 20-sided dice, then a 6-sided dice. Sum the outcomes of the dice rolls, then subtract 5.
- max(3d20) + 5 -> roll three 20-sided dice, select the greatest outcome, then add 5.
- sum(10(d4+3)) -> ten times roll a 4-sided dice and add 3. Return the sum of those values.
- sum(10d4) + 30 -> roll ten 4-sided dice, sum the values, then add 30. Arithmetically equivalent to the sum(10(d4+3)).
- 10(d10 + 2) -> ten times roll a 10-sided dice and add 2. Return the array of the outcomes of those rolls.

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