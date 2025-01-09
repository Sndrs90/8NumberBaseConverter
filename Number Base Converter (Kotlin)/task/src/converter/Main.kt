package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import kotlin.math.pow

fun main() {
    //Основания систем счисления исходного числа и результата (по умолчанию 10)
    var sourceBase = 10
    var targetBase = 10

    //Цикл для этапа ввода оснований систем счисления (1-ый уровень меню)
    while (true) {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        //Ввод пользователя
        val input = readln()
        //Рег выражение - 2 цифры для 1-ого основания с-мы счисл-я ПРОБЕЛ 2 цифры для 2-ого основания
        val regex = Regex("\\d{1,2} \\d{1,2}")
        when {
            //Если ввод соответствует рег выражению
            regex.matches(input) -> {
                //Конвертируем в 2 числа и отправляем их в список
                val inpList = input.split(" ").map { it.toInt() }
                try {
                    //Проверяем, что введенные числа в диапазоне 2..36
                    check(inpList.first() in 2..36){"Source base should be from 2 to 36"}
                    check(inpList.last() in 2..36){"Target base should be from 2 to 36"}
                    //Если всё в порядке присваиваем новые значения онований с-м счисл-я
                    sourceBase = inpList.first()
                    targetBase = inpList.last()
                } catch (e:IllegalStateException) {
                    println(e.message)
                    continue
                }
            }
            //Если польз-ль ввёл /exit, то выход
            input == "/exit" -> break
        }

        //Цикл этапа ввода числа для конвертирования (2-ой уровень меню)
        while (true) {
            print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back) ")
            //numInput - новый ввод польз-ля
            when (val numInput = readln()) {
                //Если ввёл /back, то возврат на предыдущий уровень ввода
                "/back" -> break
                //Допущение, что польз-ль помимо /back вводит всегда корректно число
                else -> {
                    //Случай целого числа
                    if (!numInput.contains('.')) {
                        //Перевод в десятичную с-му счисл-я
                        val decimal = convertToDecimal(numInput, sourceBase)
                        //Перевод в необходимую систему из десятичной
                        val result = convertFromDecimal(decimal, targetBase)
                        println("Conversion result: $result")
                    //Случай вещественного числа
                    } else {
                        //Отделяем целую часть от дробной в введенном числе
                        val partsOfFractional = numInput.split(".")
                        val intPart = partsOfFractional[0]
                        val fracPart = partsOfFractional[1]
                        //Конвертируем целую часть числа
                        val decimalIntPart = convertToDecimal(intPart, sourceBase)
                        val intPartConverted = convertFromDecimal(decimalIntPart, targetBase)
                        //Конвертируем дробную часть
                        val decimalFracPart = convertToDecimalFraction(fracPart,sourceBase)
                        val fracPartConverted = convertFromDecimalFraction(decimalFracPart, targetBase)
                        //Объединяем целую и дробную части
                        val conversionResult = "$intPartConverted.$fracPartConverted"
                        println("Conversion result: $conversionResult")
                    }
                }
            }
        }
    }
}

//Ф-ция перевода десятичного целого числа в необходимую систему счисления
private fun convertFromDecimal(decimal: BigInteger, targetBase: Int): String {
    //Присваиваем переданное значение 10-тичного числа
    var num = decimal
    //Переводим основание с-мы счисления в BigInteger
    val base = targetBase.toBigInteger()
    //list для собирания значений результата (здесь они в обратном порядке)
    val intList = mutableListOf<Int>()
    //Случай когда целое число 0
    if (num == BigInteger.ZERO) return "0"
    //Алгоритм перевода делением столбиком если целое число != 0
    while (num > 0.toBigInteger()) {
        intList.add((num % base).toInt())
        num /= base
    }
    //Переворачиваем значения в листе и заменяем числа >9 на буквы
    val result = intList.reversed().map {
        when {
            it < 10 -> it.toString()
            else -> intToLetter(it)
        }
    }
    //Вывод результата в виде строки
    return result.joinToString("")
}

//Ф-ция перевода заданного целого числа в десятичную с-му
private fun convertToDecimal(number: String, sourceBase: Int): BigInteger {
    //Переводим основание в Double, чтобы затем воспользоваться pow()
    val base = sourceBase.toDouble()
    //Исходное число переворачиваем, конвертируем в лист, буквы заменяем числовыми значениями
    val reversedDigits: List<Int> = number.reversed().toCharArray().map{
        when  {
            it.isDigit() -> it.digitToInt()
            it.isLetter() -> letterToInt(it)
            else -> 0
        }
    }
    //Начальное значение для результата
    var decimalNumber = 0.toBigInteger()
    //Цикл алгоритма перевода в десятичную с-му счисления
    for ((i, n) in reversedDigits.withIndex()) {
        decimalNumber += (n.toBigInteger() * base.pow(i).toBigDecimal().toBigInteger())
    }
    //Возврат результата
    return decimalNumber
}

//Ф-ция перевода из 10-тичной системы в заданную для дробной части числа
private fun convertFromDecimalFraction(decimalFracPart: BigDecimal, targetBase: Int): String {
    //Лист для накопления результата
    val resultList: MutableList<Int> = mutableListOf()
    //Дробная часть числа (меняется в ходе алгоритма)
    var fracPart = decimalFracPart
    //Основание исходной системы счисления конвертируем в BigDecimal
    val base = targetBase.toBigDecimal()

    //Пока не будет 5 цифр после запятой (заданная точность в задаче)
    while (resultList.size != 5) {
        //Временная перем-ая для разделения целой и дробной части при выполнении алгоритма
        val tempNumber = (fracPart * base).divideAndRemainder(BigDecimal.ONE)
        //Целая часть, которая идет в результат
        val intPart = tempNumber[0].toInt()
        //Дробная часть, которая нужна для продолжения алгоритма
        fracPart = tempNumber[1]
        //Добавляем в лист результата
        resultList.add(intPart)
    }

    //Конвертация листа в строку, замена цифр > 9 на букву
    val resultWithLettersString = resultList.map {
        when {
            it < 10 -> it.toString()
            else -> intToLetter(it)
        }
    }.joinToString("")
    //Вывод результата
    return resultWithLettersString
}

//Ф-ция перевода в 10-тичную систему из заданной дробной части
private fun convertToDecimalFraction(fracPart: String, sourceBase: Int) : BigDecimal {
    //Основание исходной с-мы счисл-я в виде BigDecimal
    val base = sourceBase.toBigDecimal()
    //Представляем дробную часть в виде листа, буквы заменяем числами
    val fracPartDigits: List<Int> = fracPart.toCharArray().map{
        when  {
            it.isDigit() -> it.digitToInt()
            it.isLetter() -> letterToInt(it)
            else -> 0
        }
    }
    //Начальное значение результата
    var decimalFracNumber = 0.toBigDecimal()
    //Для степени, в которую возводить основание
    var i = 1
    //Алгоритм конвертации в 10-тичную систему
    for (n in fracPartDigits) {
        //Используем divide т.к. не работает правильно /
        //Необходимо указать scale и RoundingMode, чтобы не поймать бесконечное деление
        decimalFracNumber += n.toBigDecimal().divide(base.pow(i), 8, RoundingMode.HALF_UP)
        ++i
    }
    //Вывод результата
    return decimalFracNumber
}

//Конвертация Char -> Int
private fun letterToInt(ch: Char) : Int = ch.code - 87

//Конвертация Int -> Char
private fun intToLetter(num: Int) : Char = (num + 87).toChar()