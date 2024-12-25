package converter

import java.math.BigInteger
import kotlin.math.pow

fun main() {
    var sourceBase = 10
    var targetBase = 10

    while (true) {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        val input = readln()
        val regex = Regex("\\d{1,2} \\d{1,2}")
        when {
            regex.matches(input) -> {
                val inpList = input.split(" ").map { it.toInt() }
                try {
                    check(inpList.first() in 2..36){"Source base should be from 2 to 36"}
                    check(inpList.last() in 2..36){"Target base should be from 2 to 36"}
                    sourceBase = inpList.first()
                    targetBase = inpList.last()
                } catch (e:IllegalStateException) {
                    println(e.message)
                    continue
                }
            }
            input == "/exit" -> break
        }

        while (true) {
            print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back) ")
            val numInput = readln()
            when (numInput) {
                "/back" -> break
                else -> {
                    val decimal = convertToDecimal(numInput, sourceBase)
                    convertFromDecimal(decimal, targetBase)
                }
            }
        }
    }
}

private fun convertFromDecimal(decimal: BigInteger, targetBase: Int) {
    var num = decimal
    val base = targetBase.toBigInteger()

    val intList = mutableListOf<Int>()
    while (num > 0.toBigInteger()) {
        intList.add((num % base).toInt())
        num /= base
    }
    val result = intList.reversed().map {
        when {
            it < 10 -> it.toString()
            else -> intToLetter(it)
        }
    }
    println("Conversion result: ${result.joinToString("")}")
}

private fun convertToDecimal(number: String, sourceBase: Int): BigInteger {
    val base = sourceBase.toDouble()
    val reversedDigits: List<Int> = number.reversed().toCharArray().map{
        when  {
            it.isDigit() -> it.digitToInt()
            it.isLetter() -> letterToInt(it)
            else -> 0
        }
    }
    var decimalNumber = 0.toBigInteger()
    for ((i, n) in reversedDigits.withIndex()) {
        decimalNumber += (n.toBigInteger() * base.pow(i).toBigDecimal().toBigInteger())
    }
    return decimalNumber
}

private fun letterToInt(ch: Char) : Int = ch.code - 87

private fun intToLetter(num: Int) : Char = (num + 87).toChar()