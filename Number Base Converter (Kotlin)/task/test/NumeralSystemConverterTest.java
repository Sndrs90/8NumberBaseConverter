package hstest;

import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;
import org.hyperskill.hstest.testing.TestedProgram;

import java.util.Arrays;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

public class NumeralSystemConverterTest extends StageTest<String> {

    @Override
    public List<TestCase<String>> generate() {

        return Arrays.asList(
            new TestCase<String>().setDynamicTesting(this::test1),
            new TestCase<String>().setDynamicTesting(this::test2),
            new TestCase<String>().setDynamicTesting(this::test3),
            new TestCase<String>().setTimeLimit(60000).setDynamicTesting(this::test4),
            new TestCase<String>().setTimeLimit(60000).setDynamicTesting(this::test5),
            new TestCase<String>().setTimeLimit(60000).setDynamicTesting(this::test6),
            new TestCase<String>().setTimeLimit(60000).setDynamicTesting(this::test7)
        );
    }

    //test exit command
    CheckResult test1() {

        TestedProgram main = new TestedProgram();
        String output = main.start().toLowerCase();

        if (!output.contains("source base") || !output.contains("target base") || !output.contains("/exit")) {
            return CheckResult.wrong("Your program should output the message \"Enter two numbers in format:" +
                " {source base} {target base} (To quit type /exit)\" when it starts");
        }

        main.execute("/exit");
        if (!main.isFinished()) {
            return CheckResult.wrong("Your program should terminate when the user enters " +
                "\"/exit\"");
        }

        return CheckResult.correct();
    }

    //test output format
    CheckResult test2() {
        TestedProgram main = new TestedProgram();
        String output;
        String randomDecimal;
        String actualResult;
        String userResult;
        String lastLine;
        String[] lines;

        main.start();
        output = main.execute("10 2").toLowerCase();
        if (!output.contains("base 10") || !output.contains("convert to base 2")) {
            return CheckResult.wrong("Your program should prompt the user for the number to be " +
                "converted with the message \"Enter number in base " +
                "{user source base} to convert to base {user target base}" +
                " (To go back type /back)\" after accepting the " +
                "source and target base");
        }

        if (!output.contains("/back")) {
            return CheckResult.wrong("Your program should provide the user with an option to go " +
                "back to the top-level menu with the message \"Enter number in base " +
                "{user source base} to convert to base {user target base} " +
                "(To go back type /back)\"");
        }

        randomDecimal = Generator_HsTest.getRandomSourceNumber(10);
        actualResult = Converter_HsTest.convertDecimalToBaseX(randomDecimal, 2);

        output = main.execute(randomDecimal).toLowerCase();
        lines = output.split("\n");
        lastLine = lines[lines.length - 1];

        if (!lines[0].contains("result:")) {
            return CheckResult.wrong("Your program should print the conversion result in the " +
                "format \"Conversion result: CONVERTED_NUMBER\"");
        }

        if (!lines[0].contains(":")) {
            return CheckResult.wrong("After entering the target base the next line doesn't contain a conversion result!\n" +
                "It should contain ':' symbol!\n" +
                "Your line\n:" + lines[0]);
        }

        userResult = lines[0].substring(output.indexOf(":") + 1).trim();
        if (!userResult.equalsIgnoreCase(actualResult)) {
            return CheckResult.wrong("The conversion result of your program is wrong");
        }

        if (main.isFinished()) {
            return CheckResult.wrong("Your program should not terminate until the user enter " +
                "\"/exit\" in the top-level menu");
        }

        if (lastLine.contains("/exit")) {
            return CheckResult.wrong("Your program should remember the user's source and target " +
                "base and should not return to the top-level menu " +
                "until the user enters \"/back\"");
        }

        if (!lastLine.contains("base 10") || !lastLine.contains("convert to base 2")) {
            return CheckResult.wrong("After each conversion, your program should prompt the user" +
                " for a number to be " +
                "converted with the message \"Enter number in base " +
                "{user source base} to convert to base {user target base}" +
                " (To go back type /back)\" until the user enters " +
                "\"/back\"");
        }


        return CheckResult.correct();
    }

    //test back command
    CheckResult test3() {
        TestedProgram main = new TestedProgram();
        String output;
        String lastLine;
        String[] lines;


        main.start();
        main.execute("10 2");
        main.execute(Generator_HsTest.getRandomSourceNumber(10));

        output = main.execute("/back").toLowerCase();
        if (!output.contains("/exit")) {
            return CheckResult.wrong("Your program should take the user back to the top-level " +
                "menu when they enter \"/back\"");
        }

        main.execute("10 8");
        output = main.execute(Generator_HsTest.getRandomSourceNumber(10)).toLowerCase().trim();

        lines = output.split("\n");
        lastLine = lines[lines.length - 1];
        if (!lastLine.contains("base 10") || !lastLine.contains("convert to base 8")) {
            return CheckResult.wrong("After each conversion, your program should prompt the user" +
                " for a number to be " +
                "converted with the message \"Enter number in base " +
                "{user source base} to convert to base {user target base}" +
                " (To go back type /back)\" until the user enters " +
                "\"/back\"");
        }

        main.execute("/back");
        main.execute("/exit");
        if (!main.isFinished()) {
            return CheckResult.wrong("Your program should terminate when the user enters " +
                "\"/exit\"");
        }

        return CheckResult.correct();
    }

    //convert every possible base to every possible base ;)
    CheckResult test4() {
        TestedProgram main = new TestedProgram();
        String output;
        String lastLine;
        String userResult;
        String actualResult;
        String randomSourceNumber;
        String[] lines;

        main.start();

        for (int sourceBase = 2; sourceBase <= 18; sourceBase += 3) {

            for (int targetBase = 2; targetBase <= 36; targetBase += 3) {

                if (sourceBase == targetBase) {
                    continue;
                }

                output = main.execute(sourceBase + " " + targetBase).toLowerCase();
                if (!output.contains("base " + sourceBase) || !output.contains("convert to base " + targetBase)) {
                    return CheckResult.wrong("Your program should prompt the user for the number to be " +
                        "converted with the message \"Enter number in base " +
                        "{user source base} to convert to base {user target base}" +
                        " (To go back type /back)\" after accepting the " +
                        "source and target base");
                }

                if (!output.contains("/back")) {
                    return CheckResult.wrong("Your program should provide the user with an option to go " +
                        "back to the top-level menu with the message \"Enter number in base " +
                        "{user source base} to convert to base {user target base} " +
                        "(To go back type /back)\"");
                }

                randomSourceNumber = Generator_HsTest.getRandomSourceNumber(sourceBase);
                actualResult = Converter_HsTest
                    .convertSourceToTargetBase(randomSourceNumber, sourceBase, targetBase);

                output = main.execute(randomSourceNumber).toLowerCase();

                lines = output.split("\n");
                lastLine = lines[lines.length - 1];

                if (!lines[0].contains(":")) {
                    return CheckResult.wrong("After entering the target base the next line doesn't contain a conversion result!\n" +
                        "It should contain ':' symbol!\n" +
                        "Your line\n:" + lines[0]);
                }

                userResult = lines[0].substring(output.indexOf(":") + 1).trim();
                if (!userResult.equalsIgnoreCase(actualResult)) {
                    return CheckResult.wrong("The conversion result of your program is wrong");
                }

                if (main.isFinished()) {
                    return CheckResult.wrong("Your program should not terminate until the user enter " +
                        "\"/exit\" in the top-level menu");
                }

                if (lastLine.contains("/exit")) {
                    return CheckResult.wrong("Your program should remember the user's source and target " +
                        "base and should not return to the top-level menu " +
                        "until the user enters \"/back\"");
                }

                main.execute("/back");
            }

        }

        main.execute("/exit");
        if (!main.isFinished()) {
            return CheckResult.wrong("Your program should terminate when the user enters " +
                "\"/exit\"");
        }

        return CheckResult.correct();
    }

    CheckResult test5() {
        TestedProgram main = new TestedProgram();
        String output;
        String lastLine;
        String userResult;
        String actualResult;
        String randomSourceNumber;
        String[] lines;

        main.start();

        for (int sourceBase = 19; sourceBase <= 36; sourceBase += 3) {

            for (int targetBase = 2; targetBase <= 36; targetBase += 3) {

                if (sourceBase == targetBase) {
                    continue;
                }

                output = main.execute(sourceBase + " " + targetBase).toLowerCase();
                if (!output.contains("base " + sourceBase) || !output.contains("convert to base " + targetBase)) {
                    return CheckResult.wrong("Your program should prompt the user for the number to be " +
                        "converted with the message \"Enter number in base " +
                        "{user source base} to convert to base {user target base}" +
                        " (To go back type /back)\" after accepting the " +
                        "source and target base");
                }

                if (!output.contains("/back")) {
                    return CheckResult.wrong("Your program should provide the user with an option to go " +
                        "back to the top-level menu with the message \"Enter number in base " +
                        "{user source base} to convert to base {user target base} " +
                        "(To go back type /back)\"");
                }

                randomSourceNumber = Generator_HsTest.getRandomSourceNumber(sourceBase);
                actualResult = Converter_HsTest
                    .convertSourceToTargetBase(randomSourceNumber, sourceBase, targetBase);

                output = main.execute(randomSourceNumber).toLowerCase();

                lines = output.split("\n");
                lastLine = lines[lines.length - 1];

                if (!lines[0].contains(":")) {
                    return CheckResult.wrong("After entering the target base the next line doesn't contain a conversion result!\n" +
                        "It should contain ':' symbol!\n" +
                        "Your line\n:" + lines[0]);
                }

                userResult = lines[0].substring(output.indexOf(":") + 1).trim();
                if (!userResult.equalsIgnoreCase(actualResult)) {
                    return CheckResult.wrong("The conversion result of your program is wrong");
                }

                if (main.isFinished()) {
                    return CheckResult.wrong("Your program should not terminate until the user enter " +
                        "\"/exit\" in the top-level menu");
                }

                if (lastLine.contains("/exit")) {
                    return CheckResult.wrong("Your program should remember the user's source and target " +
                        "base and should not return to the top-level menu " +
                        "until the user enters \"/back\"");
                }

                main.execute("/back");
            }

        }

        main.execute("/exit");
        if (!main.isFinished()) {
            return CheckResult.wrong("Your program should terminate when the user enters " +
                "\"/exit\"");
        }

        return CheckResult.correct();
    }

    //using BigInteger
    CheckResult test6() {
        TestedProgram main = new TestedProgram();
        String output;
        String lastLine;
        String userResult;
        String actualResult;
        String randomBigInteger;
        String[] lines;

        main.start();

        for (int sourceBase = 2; sourceBase <= 18; sourceBase += 3) {

            for (int targetBase = 2; targetBase <= 36; targetBase +=3) {

                if (sourceBase == targetBase) {
                    continue;
                }

                output = main.execute(sourceBase + " " + targetBase).toLowerCase();
                if (!output.contains("base " + sourceBase) || !output.contains("convert to base " + targetBase)) {
                    return CheckResult.wrong("Your program should prompt the user for the number to be " +
                        "converted with the message \"Enter number in base " +
                        "{user source base} to convert to base {user target base}" +
                        " (To go back type /back)\" after accepting the " +
                        "source and target base");
                }

                if (!output.contains("/back")) {
                    return CheckResult.wrong("Your program should provide the user with an option to go " +
                        "back to the top-level menu with the message \"Enter number in base " +
                        "{user source base} to convert to base {user target base} " +
                        "(To go back type /back)\"");
                }

                randomBigInteger = Generator_HsTest.getRandomBigInteger(sourceBase);
                actualResult = Converter_HsTest
                    .convertSourceToTargetBase(randomBigInteger, sourceBase, targetBase);

                output = main.execute(randomBigInteger).toLowerCase();

                lines = output.split("\n");
                lastLine = lines[lines.length - 1];

                if (!lines[0].contains(":")) {
                    return CheckResult.wrong("After entering the target base the next line doesn't contain a conversion result!\n" +
                        "It should contain ':' symbol!\n" +
                        "Your line\n:" + lines[0]);
                }

                userResult = lines[0].substring(output.indexOf(":") + 1).trim();
                if (!userResult.equalsIgnoreCase(actualResult)) {
                    return CheckResult.wrong("The conversion result of your program is wrong");
                }

                if (main.isFinished()) {
                    return CheckResult.wrong("Your program should not terminate until the user enter " +
                        "\"/exit\" in the top-level menu");
                }

                if (lastLine.contains("/exit")) {
                    return CheckResult.wrong("Your program should remember the user's source and target " +
                        "base and should not return to the top-level menu " +
                        "until the user enters \"/back\"");
                }

                main.execute("/back");
            }

        }

        main.execute("/exit");
        if (!main.isFinished()) {
            return CheckResult.wrong("Your program should terminate when the user enters " +
                "\"/exit\"");
        }

        return CheckResult.correct();
    }

    CheckResult test7() {
        TestedProgram main = new TestedProgram();
        String output;
        String lastLine;
        String userResult;
        String actualResult;
        String randomBigInteger;
        String[] lines;

        main.start();

        for (int sourceBase = 19; sourceBase <= 36; sourceBase += 3) {

            for (int targetBase = 2; targetBase <= 36; targetBase += 3) {

                if (sourceBase == targetBase) {
                    continue;
                }

                output = main.execute(sourceBase + " " + targetBase).toLowerCase();
                if (!output.contains("base " + sourceBase) || !output.contains("convert to base " + targetBase)) {
                    return CheckResult.wrong("Your program should prompt the user for the number to be " +
                        "converted with the message \"Enter number in base " +
                        "{user source base} to convert to base {user target base}" +
                        " (To go back type /back)\" after accepting the " +
                        "source and target base");
                }

                if (!output.contains("/back")) {
                    return CheckResult.wrong("Your program should provide the user with an option to go " +
                        "back to the top-level menu with the message \"Enter number in base " +
                        "{user source base} to convert to base {user target base} " +
                        "(To go back type /back)\"");
                }

                randomBigInteger = Generator_HsTest.getRandomBigInteger(sourceBase);
                actualResult = Converter_HsTest
                    .convertSourceToTargetBase(randomBigInteger, sourceBase, targetBase);

                output = main.execute(randomBigInteger).toLowerCase();

                lines = output.split("\n");
                lastLine = lines[lines.length - 1];

                if (!lines[0].contains(":")) {
                    return CheckResult.wrong("After entering the target base the next line doesn't contain a conversion result!\n" +
                        "It should contain ':' symbol!\n" +
                        "Your line\n:" + lines[0]);
                }

                userResult = lines[0].substring(output.indexOf(":") + 1).trim();
                if (!userResult.equalsIgnoreCase(actualResult)) {
                    return CheckResult.wrong("The conversion result of your program is wrong");
                }

                if (main.isFinished()) {
                    return CheckResult.wrong("Your program should not terminate until the user enter " +
                        "\"/exit\" in the top-level menu");
                }

                if (lastLine.contains("/exit")) {
                    return CheckResult.wrong("Your program should remember the user's source and target " +
                        "base and should not return to the top-level menu " +
                        "until the user enters \"/back\"");
                }

                main.execute("/back");
            }

        }

        main.execute("/exit");
        if (!main.isFinished()) {
            return CheckResult.wrong("Your program should terminate when the user enters " +
                "\"/exit\"");
        }

        return CheckResult.correct();
    }

}

class Generator_HsTest {
    static String getRandomBigInteger(int sourceBase) {
        BigInteger upperLimit = new BigInteger("500000000000000");
        BigInteger randomNumber;
        do {
            randomNumber = new BigInteger(upperLimit.bitLength(), new Random());
        } while (randomNumber.compareTo(upperLimit) >= 0);

        return randomNumber.toString(sourceBase);
    }

    static String getRandomSourceNumber(int sourceBase) {

        int n = new Random().nextInt(1000);

        return Integer.toString(n, sourceBase);
    }
}

class Converter_HsTest {
    static String convertDecimalToBaseX(String num, int targetBase) {
        return new BigInteger(String.valueOf(num)).toString(targetBase);
    }

    static String convertBaseXToDecimal(String number, int sourceBase) {
        BigInteger integer = new BigInteger(number, sourceBase);
        return integer.toString();
    }

    static String convertSourceToTargetBase(String number, int sourceBase, int targetBase) {
        //Step 1 - convert source number to decimal
        String decimal = convertBaseXToDecimal(number, sourceBase);

        //Step 2 - convert decimal to target base
        return convertDecimalToBaseX(decimal, targetBase);
    }
}
