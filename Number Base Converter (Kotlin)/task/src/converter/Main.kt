package converter

import kotlin.math.pow

val hexLetters = mapOf(
    "a" to 10, "b" to 11, "c" to 12,
    "d" to 13, "e" to 14, "f" to 15
)

val hexLettersInversion = mapOf(
    10 to "a", 11 to "b", 12 to "c",
    13 to "d", 14 to "e", 15 to "f"
)

fun main() {
    while (true) {
        print("Do you want to convert /from decimal or /to decimal (To quit type /exit) ")
        val input = readln()
        when (input) {
            "/from" -> convertFromDecimal()
            "/to" -> convertToDecimal()
            "/exit" -> break
        }
    }
}

private fun convertFromDecimal() {
    print("Enter number in decimal system: ")
    var num = readln().toInt()
    print("Enter target base: ")
    val base = readln().toInt()

    val intList = mutableListOf<Int>()
    while (num > 0) {
        intList.add(num % base)
        num /= base
    }
    val result = intList.reversed().map {
        when {
            it < 10 -> it.toString()
            else -> hexLettersInversion[it]
        }
    }
    println("Conversion result: ${result.joinToString("")}")
}

private fun convertToDecimal() {
    print("Enter source number: ")
    val num = readln()
    print("Enter source base: ")
    val base = readln().toDouble()
    val reversedDigits: List<Int> = num.reversed().toCharArray().map{
        when  {
            it.isDigit() -> it.digitToInt()
            it.isLetter() -> hexLetters[it.lowercase()] ?: 0
            else -> 0
        }
    }
    var decimalNumber = 0
    var i = 0
    for (n in reversedDigits) {
        decimalNumber += (n * base.pow(i)).toInt()
        ++i
    }
    println("Conversion to decimal result: $decimalNumber")
}