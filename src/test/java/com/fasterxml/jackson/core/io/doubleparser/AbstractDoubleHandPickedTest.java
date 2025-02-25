/**
 * References:
 * <dl>
 *     <dt>This class has been derived from "FastDoubleParser".</dt>
 *     <dd>Copyright (c) Werner Randelshofer. Apache 2.0 License.
 *         <a href="https://github.com/wrandelshofer/FastDoubleParser">github.com</a>.</dd>
 * </dl>
 */

package com.fasterxml.jackson.core.io.doubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

abstract class AbstractDoubleHandPickedTest {

    /**
     * Tests input classes that execute different code branches in the parse
     * methods of {@link AbstractFloatValueFromCharSequence}.
     * <p>
     * This test must achieve 100 % line coverage of the tested class.
     */
    @TestFactory
    List<DynamicNode> dynamicTestsDecFloatLiteralParserInputClasses() {
        return Arrays.asList(
                dynamicTest("parseFloatValue(): Significand without whitespace", () -> testLegalInput("3")),
                dynamicTest("parseFloatValue(): Significand with leading whitespace", () -> testLegalInput("   3")),
                dynamicTest("parseFloatValue(): Significand with trailing whitespace", () -> testLegalInput("3   ")),
                dynamicTest("parseFloatValue(): Empty String", () -> testIllegalInput("")),
                dynamicTest("parseFloatValue(): Blank String", () -> testIllegalInput("   ")),
                dynamicTest("parseFloatValue(): Very long non-blank String", () -> testIllegalInput("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")),
                dynamicTest("parseFloatValue(): Plus Sign", () -> testIllegalInput("+")),
                dynamicTest("parseFloatValue(): Negative Sign", () -> testIllegalInput("-")),
                dynamicTest("parseFloatValue(): Infinity", () -> testLegalInput("Infinity")),
                dynamicTest("parseFloatValue(): NaN", () -> testLegalInput("NaN")),
                dynamicTest("parseInfinity(): Infinit (missing last char)", () -> testIllegalInput("Infinit")),
                dynamicTest("parseInfinity(): InfinitY (bad last char)", () -> testIllegalInput("InfinitY")),
                dynamicTest("parseNaN(): Na (missing last char)", () -> testIllegalInput("Na")),
                dynamicTest("parseNaN(): Nan (bad last char)", () -> testIllegalInput("Nan")),
                dynamicTest("parseFloatValue(): Leading zero", () -> testLegalInput("03")),
                dynamicTest("parseFloatValue(): Leading zero x", () -> testIllegalInput("0x3")),
                dynamicTest("parseFloatValue(): Leading zero X", () -> testIllegalInput("0X3")),

                dynamicTest("parseDecFloatLiteral(): With decimal point", () -> testLegalInput("3.")),
                dynamicTest("parseDecFloatLiteral(): Without decimal point", () -> testLegalInput("3")),
                dynamicTest("parseDecFloatLiteral(): 7 digits after decimal point", () -> testLegalInput("3.1234567")),
                dynamicTest("parseDecFloatLiteral(): 8 digits after decimal point", () -> testLegalInput("3.12345678")),
                dynamicTest("parseDecFloatLiteral(): 9 digits after decimal point", () -> testLegalInput("3.123456789")),
                dynamicTest("parseDecFloatLiteral(): 1 digit + 7 chars after decimal point", () -> testIllegalInput("3.1abcdefg")),
                dynamicTest("parseDecFloatLiteral(): With 'e' at end", () -> testIllegalInput("3e")),
                dynamicTest("parseDecFloatLiteral(): With 'E' at end", () -> testIllegalInput("3E")),
                dynamicTest("parseDecFloatLiteral(): With 'e' + whitespace at end", () -> testIllegalInput("3e   ")),
                dynamicTest("parseDecFloatLiteral(): With 'E' + whitespace  at end", () -> testIllegalInput("3E   ")),
                dynamicTest("parseDecFloatLiteral(): With 'e+' at end", () -> testIllegalInput("3e+")),
                dynamicTest("parseDecFloatLiteral(): With 'E-' at end", () -> testIllegalInput("3E-")),
                dynamicTest("parseDecFloatLiteral(): With 'e+9' at end", () -> testLegalInput("3e+9")),
                dynamicTest("parseDecFloatLiteral(): With 20 significand digits", () -> testLegalInput("12345678901234567890")),
                dynamicTest("parseDecFloatLiteral(): With 20 significand digits + non-ascii char", () -> testIllegalInput("12345678901234567890￡")),
                dynamicTest("parseDecFloatLiteral(): With 20 significand digits with decimal point", () -> testLegalInput("1234567890.1234567890")),

                dynamicTest("parseHexFloatLiteral(): With decimal point", () -> testIllegalInput("0x3.")),
                dynamicTest("parseHexFloatLiteral(): Without decimal point", () -> testIllegalInput("0X3")),
                dynamicTest("parseHexFloatLiteral(): 7 digits after decimal point", () -> testIllegalInput("0x3.1234567")),
                dynamicTest("parseHexFloatLiteral(): 8 digits after decimal point", () -> testIllegalInput("0X3.12345678")),
                dynamicTest("parseHexFloatLiteral(): 9 digits after decimal point", () -> testIllegalInput("0x3.123456789")),
                dynamicTest("parseHexFloatLiteral(): 1 digit + 7 chars after decimal point", () -> testIllegalInput("0X3.1abcdefg")),
                dynamicTest("parseHexFloatLiteral(): With 'p' at end", () -> testIllegalInput("0X3p")),
                dynamicTest("parseHexFloatLiteral(): With 'P' at end", () -> testIllegalInput("0x3P")),
                dynamicTest("parseHexFloatLiteral(): With 'p' + whitespace at end", () -> testIllegalInput("0X3p   ")),
                dynamicTest("parseHexFloatLiteral(): With 'P' + whitespace  at end", () -> testIllegalInput("0x3P   ")),
                dynamicTest("parseHexFloatLiteral(): With 'p+' at end", () -> testIllegalInput("0X3p+")),
                dynamicTest("parseHexFloatLiteral(): With 'P-' at end", () -> testIllegalInput("0x3P-")),
                dynamicTest("parseHexFloatLiteral(): With 'p+9' at end", () -> testLegalInput("0X3p+9")),
                dynamicTest("parseHexFloatLiteral(): With 20 significand digits", () -> testLegalInput("0x12345678901234567890p0")),
                dynamicTest("parseHexFloatLiteral(): With 20 significand digits + non-ascii char", () -> testIllegalInput("0x12345678901234567890￡p0")),
                dynamicTest("parseHexFloatLiteral(): With 20 significand digits with decimal point", () -> testLegalInput("0x1234567890.1234567890P0"))
        );
    }

    /**
     * Tests input classes that execute different code branches in
     * method {@link FastDoubleMath#tryDecFloatToDouble(boolean, long, int)}.
     * <p>
     * This test must achieve 100 % line coverage of the tested method.
     */
    @TestFactory
    List<DynamicNode> dynamicTestsDecFloatLiteralClingerInputClasses() {
        return Arrays.asList(
                dynamicTest("Inside Clinger fast path \"1000000000000000000e-340\")", () -> testLegalInput("1000000000000000000e-325")),
                //
                dynamicTest("Inside Clinger fast path (max_clinger_significand, max_clinger_exponent)", () -> testLegalInput("9007199254740991e22")),
                dynamicTest("Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", () -> testLegalInput("9007199254740991e23")),
                dynamicTest("Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", () -> testLegalInput("9007199254740992e22")),
                dynamicTest("Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", () -> testLegalInput("1e-22")),
                dynamicTest("Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", () -> testLegalInput("1e-23")),
                dynamicTest("Outside Clinger fast path, bail-out in semi-fast path, 1e23", () -> testLegalInput("1e23")),
                dynamicTest("Outside Clinger fast path, mantissa overflows in semi-fast path, 7.2057594037927933e+16", () -> testLegalInput("7.2057594037927933e+16")),
                dynamicTest("Outside Clinger fast path, bail-out in semi-fast path, 7.3177701707893310e+15", () -> testLegalInput("7.3177701707893310e+15"))
        );
    }

    /**
     * Tests input classes that execute different code branches in
     * method {@link FastDoubleMath#tryHexFloatToDouble(boolean, long, int)}.
     * <p>
     * This test must achieve 100 % line coverage of the tested method.
     */
    @TestFactory
    List<DynamicNode> dynamicTestsHexFloatLiteralClingerInputClasses() {
        return Arrays.asList(
                dynamicTest("Inside Clinger fast path (max_clinger_significand)", () -> testLegalInput("0x1fffffffffffffp74", 0x1fffffffffffffp74)),
                dynamicTest("Inside Clinger fast path (max_clinger_significand), negative", () -> testLegalInput("-0x1fffffffffffffp74", -0x1fffffffffffffp74)),
                dynamicTest("Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", () -> testLegalInput("0x1fffffffffffffp74", 0x1fffffffffffffp74)),
                dynamicTest("Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", () -> testLegalInput("0x20000000000000p74", 0x20000000000000p74)),
                dynamicTest("Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", () -> testLegalInput("0x1p-74", 0x1p-74)),
                dynamicTest("Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", () -> testLegalInput("0x1p-75", 0x1p-75))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsIllegalInputs() {
        return Arrays.asList(
                dynamicTest("0.+(char)0x3231+(char)0x0000+345678",
                        () -> testIllegalInput("0." + (char) 0x3231 + (char) 0x0000 + "345678")),

                dynamicTest("empty", () -> testIllegalInput("")),
                dynamicTest("-", () -> testIllegalInput("-")),
                dynamicTest("+", () -> testIllegalInput("+")),
                dynamicTest("1e", () -> testIllegalInput("1e")),
                dynamicTest("1ee2", () -> testIllegalInput("1ee2")),
                dynamicTest("1_000", () -> testIllegalInput("1_000")),
                dynamicTest("0.000_1", () -> testIllegalInput("0.000_1")),
                dynamicTest("-e-55", () -> testIllegalInput("-e-55")),
                dynamicTest("1 x", () -> testIllegalInput("1 x")),
                dynamicTest("x 1", () -> testIllegalInput("x 1")),
                dynamicTest("1§", () -> testIllegalInput("1§")),
                dynamicTest("NaN x", () -> testIllegalInput("NaN x")),
                dynamicTest("Infinity x", () -> testIllegalInput("Infinity x")),
                dynamicTest("0x123.456789abcde", () -> testIllegalInput("0x123.456789abcde")),
                dynamicTest(".", () -> testIllegalInput(".")),
                dynamicTest("0x.", () -> testIllegalInput("0x.")),
                dynamicTest(".e2", () -> testIllegalInput(".e2"))
        );
    }


    @TestFactory
    List<DynamicNode> dynamicTestsIllegalInputsWithPrefixAndSuffix() {
        return Arrays.asList(
                dynamicTest("before-after", () -> testIllegalInputWithPrefixAndSuffix("before-after", 6, 1)),
                dynamicTest("before7.78$after", () -> testIllegalInputWithPrefixAndSuffix("before7.78$after", 6, 5)),
                dynamicTest("before7.78e$after", () -> testIllegalInputWithPrefixAndSuffix("before7.78e$after", 6, 6)),
                dynamicTest("before0x123$4after", () -> testIllegalInputWithPrefixAndSuffix("before0x123$4after", 6, 7)),
                dynamicTest("before0x123.4$after", () -> testIllegalInputWithPrefixAndSuffix("before0x123.4$after", 6, 8)),
                dynamicTest("before0$123.4after", () -> testIllegalInputWithPrefixAndSuffix("before0$123.4after", 6, 7))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalDecFloatLiterals() {
        return Arrays.asList(
                dynamicTest("-0.0", () -> testLegalInput("-0.0", -0.0)),
                dynamicTest("0.12345678", () -> testLegalInput("0.12345678", 0.12345678)),
                dynamicTest("1e23", () -> testLegalInput("1e23", 1e23)),
                dynamicTest("whitespace before 1", () -> testLegalInput(" 1")),
                dynamicTest("whitespace after 1", () -> testLegalInput("1 ")),
                dynamicTest("0", () -> testLegalInput("0", 0.0)),
                dynamicTest("-0", () -> testLegalInput("-0", -0.0)),
                dynamicTest("+0", () -> testLegalInput("+0", +0.0)),
                dynamicTest("-0.0", () -> testLegalInput("-0.0", -0.0)),
                dynamicTest("-0.0e-22", () -> testLegalInput("-0.0e-22", -0.0e-22)),
                dynamicTest("-0.0e24", () -> testLegalInput("-0.0e24", -0.0e24)),
                dynamicTest("0e555", () -> testLegalInput("0e555", 0.0)),
                dynamicTest("-0e555", () -> testLegalInput("-0e555", -0.0)),
                dynamicTest("1", () -> testLegalInput("1", 1.0)),
                dynamicTest("-1", () -> testLegalInput("-1", -1.0)),
                dynamicTest("+1", () -> testLegalInput("+1", +1.0)),
                dynamicTest("1e0", () -> testLegalInput("1e0", 1e0)),
                dynamicTest("1.e0", () -> testLegalInput("1.e0", 1e0)),
                dynamicTest(".8", () -> testLegalInput(".8", 0.8)),
                dynamicTest("8.", () -> testLegalInput("8.", 8.0)),
                dynamicTest("1e1", () -> testLegalInput("1e1", 1e1)),
                dynamicTest("1e+1", () -> testLegalInput("1e+1", 1e+1)),
                dynamicTest("1e-1", () -> testLegalInput("1e-1", 1e-1)),
                dynamicTest("0049", () -> testLegalInput("0049", 49)),
                dynamicTest("9999999999999999999", () -> testLegalInput("9999999999999999999", 9999999999999999999d)),
                dynamicTest("972150611626518208.0", () -> testLegalInput("972150611626518208.0", 9.7215061162651827E17)),
                dynamicTest("3.7587182468424695418288325e-309", () -> testLegalInput("3.7587182468424695418288325e-309", 3.7587182468424695418288325e-309)),
                dynamicTest("9007199254740992.e-256", () -> testLegalInput("9007199254740992.e-256", 9007199254740992.e-256)),
                dynamicTest("0.1e+3", () -> testLegalInput("0.1e+3",
                        100.0)),
                dynamicTest("0.00000000000000000000000000000000000000000001e+46",
                        () -> testLegalInput("0.00000000000000000000000000000000000000000001e+46",
                                100.0)),
                dynamicTest("10000000000000000000000000000000000000000000e+308",
                        () -> testLegalInput("10000000000000000000000000000000000000000000e+308",
                                Double.parseDouble("10000000000000000000000000000000000000000000e+308"))),
                dynamicTest("3.1415926535897932384626433832795028841971693993751", () -> testLegalInput(
                        "3.1415926535897932384626433832795028841971693993751",
                        Double.parseDouble("3.1415926535897932384626433832795028841971693993751"))),
                dynamicTest("314159265358979323846.26433832795028841971693993751e-20", () -> testLegalInput(
                        "314159265358979323846.26433832795028841971693993751e-20",
                        3.141592653589793)),
                dynamicTest("1e-326", () -> testLegalInput(
                        "1e-326", 0.0)),
                dynamicTest("1e-325", () -> testLegalInput(
                        "1e-325", 0.0)),
                dynamicTest("1e310", () -> testLegalInput(
                        "1e310", Double.POSITIVE_INFINITY)),
                dynamicTest(7.2057594037927933e+16 + "", () -> testLegalDecInput(
                        7.2057594037927933e+16)),
                dynamicTest(-7.2057594037927933e+16 + "", () -> testLegalDecInput(
                        -7.2057594037927933e+16)),
                dynamicTest(-4.8894481170331026E-173 + "", () -> testLegalDecInput(
                        -4.8894481170331026E-173)),
                dynamicTest(4.8894481170331026E-173 + "", () -> testLegalDecInput(
                        4.8894481170331026E-173)),
                dynamicTest(-4.889448117033103E-173 + "", () -> testLegalDecInput(
                        -4.889448117033103E-173)),
                dynamicTest(4.889448117033103E-173 + "", () -> testLegalDecInput(
                        4.889448117033103E-173)),
                dynamicTest(2.348957380189919E-199 + "", () -> testLegalDecInput(
                        2.348957380189919E-199)),
                dynamicTest(-2.348957380189919E-199 + "", () -> testLegalDecInput(
                        -2.348957380189919E-199)),
                dynamicTest(-6.658066127037204E87 + "", () -> testLegalDecInput(
                        -6.658066127037204E87)),
                dynamicTest(6.658066127037204E87 + "", () -> testLegalDecInput(
                        6.658066127037204E87)),
                dynamicTest(4.559067278662733E288 + "", () -> testLegalDecInput(
                        4.559067278662733E288)),
                dynamicTest(-4.559067278662733E288 + "", () -> testLegalDecInput(
                        -4.559067278662733E288))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalDecFloatLiteralsExtremeValues() {
        return Arrays.asList(
                dynamicTest(Double.toString(Double.MIN_VALUE), () -> testLegalDecInput(
                        Double.MIN_VALUE)),
                dynamicTest(Double.toString(Double.MAX_VALUE), () -> testLegalDecInput(
                        Double.MAX_VALUE)),
                dynamicTest(Double.toString(Double.POSITIVE_INFINITY), () -> testLegalDecInput(
                        Double.POSITIVE_INFINITY)),
                dynamicTest(Double.toString(Double.NEGATIVE_INFINITY), () -> testLegalDecInput(
                        Double.NEGATIVE_INFINITY)),
                dynamicTest(Double.toString(Double.NaN), () -> testLegalDecInput(
                        Double.NaN)),
                dynamicTest(Double.toString(Math.nextUp(0.0)), () -> testLegalDecInput(
                        Math.nextUp(0.0))),
                dynamicTest(Double.toString(Math.nextDown(0.0)), () -> testLegalDecInput(
                        Math.nextDown(0.0))),
                dynamicTest("Just above MAX_VALUE: 1.7976931348623159E308", () -> testLegalInput(
                        "1.7976931348623159E308", Double.POSITIVE_INFINITY)),
                dynamicTest("Just below MIN_VALUE: 2.47E-324", () -> testLegalInput(
                        "2.47E-324", 0.0))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalHexFloatLiterals() {
        return Arrays.asList(
                dynamicTest("0x0.1234ab78p0", () -> testLegalInput("0x0.1234ab78p0", 0x0.1234ab78p0)),
                dynamicTest("0x0.1234AB78p0", () -> testLegalInput("0x0.1234AB78p0", 0x0.1234AB78p0)),
                dynamicTest("0x1.0p8", () -> testLegalInput("0x1.0p8", 256)),
                dynamicTest("0x1.234567890abcdefp123", () -> testLegalInput("0x1.234567890abcdefp123", 0x1.234567890abcdefp123)),
                dynamicTest("0x1234567890.abcdefp-45", () -> testLegalInput("0x1234567890.abcdefp-45", 0x1234567890.abcdefp-45)),
                dynamicTest("0x1234567890.abcdef12p-45", () -> testLegalInput("0x1234567890.abcdef12p-45", 0x1234567890.abcdef12p-45))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalHexFloatLiteralsExtremeValues() {
        return Arrays.asList(
                dynamicTest(Double.toHexString(Double.MIN_VALUE), () -> testLegalHexInput(
                        Double.MIN_VALUE)),
                dynamicTest(Double.toHexString(Double.MAX_VALUE), () -> testLegalHexInput(
                        Double.MAX_VALUE)),
                dynamicTest(Double.toHexString(Double.POSITIVE_INFINITY), () -> testLegalHexInput(
                        Double.POSITIVE_INFINITY)),
                dynamicTest(Double.toHexString(Double.NEGATIVE_INFINITY), () -> testLegalHexInput(
                        Double.NEGATIVE_INFINITY)),
                dynamicTest(Double.toHexString(Double.NaN), () -> testLegalHexInput(
                        Double.NaN)),
                dynamicTest(Double.toString(Math.nextUp(0.0f)), () -> testLegalHexInput(
                        Math.nextUp(0.0))),
                dynamicTest(Double.toString(Math.nextDown(0.0f)), () -> testLegalHexInput(
                        Math.nextDown(0.0))),
                dynamicTest(Double.toHexString(Math.nextUp(0.0)), () -> testLegalHexInput(
                        Math.nextUp(0.0))),
                dynamicTest(Double.toHexString(Math.nextDown(0.0)), () -> testLegalHexInput(
                        Math.nextDown(0.0))),
                dynamicTest("Just above MAX_VALUE: 0x1.fffffffffffff8p1023", () -> testLegalInput(
                        "0x1.fffffffffffff8p1023", Double.POSITIVE_INFINITY)),
                dynamicTest("Just below MIN_VALUE: 0x0.00000000000008p-1022", () -> testLegalInput(
                        "0x0.00000000000008p-1022", 0.0))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalInputsWithPrefixAndSuffix() {
        return Arrays.asList(
                dynamicTest("before-1after", () -> testLegalInputWithPrefixAndSuffix("before-1after", 6, 2, -1.0)),
                dynamicTest("before7.789after", () -> testLegalInputWithPrefixAndSuffix("before7.789after", 6, 5, 7.789)),
                dynamicTest("before7.78e2after", () -> testLegalInputWithPrefixAndSuffix("before7.78e2after", 6, 6, 7.78e2)),
                dynamicTest("before0x123.4p0after", () -> testLegalInputWithPrefixAndSuffix("before0x1234p0after", 6, 8, 0x1234p0)),
                dynamicTest("before0x123.45p0after", () -> testLegalInputWithPrefixAndSuffix("before0x123.45p0after", 6, 10, 0x123.45p0)),
                dynamicTest("Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", () -> testLegalInputWithPrefixAndSuffix(
                        "before1e-23after", 6, 5, 1e-23)),
                dynamicTest("before9007199254740992.e-256after", () -> testLegalInputWithPrefixAndSuffix(
                        "before9007199254740992.e-256after", 6, 22, 9007199254740992.e-256))


        );
    }


    @TestFactory
    Stream<DynamicNode> dynamicTestsPowerOfTen() {
        return IntStream.range(-307, 309).mapToObj(i -> "1e" + i)
                .map(d -> dynamicTest(d, () -> testLegalInput(d, Double.parseDouble(d))));
    }

    abstract double parse(CharSequence str);

    protected abstract double parse(String str, int offset, int length);

    @TestFactory
    Stream<DynamicNode> testErrorCases() throws IOException {
        return Files.lines(FileSystems.getDefault().getPath("src/test/resources/data/FastDoubleParser_errorcases.txt"))
                .flatMap(line -> Arrays.stream(line.split(",")))
                .map(str -> dynamicTest(str, () -> testLegalInput(str, Double.parseDouble(str))));
    }

    private void testIllegalInput(String s) {
        try {
            parse(s);
            fail();
        } catch (NumberFormatException e) {
            // success
        }
    }

    private void testIllegalInputWithPrefixAndSuffix(String str, int offset, int length) {
        try {
            parse(str, offset, length);
            fail();
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            assertFalse(message.contains(str.substring(0, offset)), "Message must not contain prefix. message=" + message);
            assertFalse(message.contains(str.substring(offset + length)), "Message must not contain suffix. message=" + message);
            assertTrue(message.contains(str.substring(offset, offset + length)), "Message must contain body. message=" + message);
        }
    }

    private void testLegalDecInput(double expected) {
        testLegalInput(expected + "", expected);
    }

    private void testLegalHexInput(double expected) {
        testLegalInput(Double.toHexString(expected), expected);
    }

    private void testLegalInput(String str) {
        testLegalInput(str, Double.parseDouble(str));
    }

    private void testLegalInput(String str, double expected) {
        double actual = parse(str);
        assertEquals(expected, actual, "str=" + str);
        assertEquals(Double.doubleToLongBits(expected), Double.doubleToLongBits(actual),
                "longBits of " + expected);
    }

    private void testLegalInputWithPrefixAndSuffix(String str, int offset, int length, double expected) {
        double actual = parse(str, offset, length);
        assertEquals(expected, actual);
    }

}
