package service;

import exception.CalculatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CalculatorTests {
    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    void add_shouldReturnZero_forEmptyInput() {
        assertThat(calculator.add("")).isEqualTo(0);
    }

    @Test
    void add_shouldReturnSingleNumber() {
        assertThat(calculator.add("1")).isEqualTo(1);
    }

    @Test
    void add_shouldReturnSum_forTwoValidNumbers() {
        assertThat(calculator.add("1,2")).isEqualTo(3);
    }

    @Test
    void add_shouldHandleMultipleNumbersWithCommasAndNewlines() {
        assertThat(calculator.add("1,2\n3")).isEqualTo(6);
    }

    @Test
    void add_shouldThrowException_forInvalidMixedSeparators() {
        assertThatThrownBy(() -> calculator.add("2,\n3"))
                .isInstanceOf(CalculatorException.class)
                .hasMessageContaining("Missing number before delimiter at position 2.");
    }

    @Test
    void add_shouldThrowException_forTrailingSeparator() {
        assertThatThrownBy(() -> calculator.add("1,2,"))
                .isInstanceOf(CalculatorException.class)
                .hasMessageContaining("Separator at the end is not allowed");
    }

    @Test
    void add_shouldSupportCustomDelimiter() {
        assertThat(calculator.add("//;\n1;2")).isEqualTo(3);
        assertThat(calculator.add("//|\n1|2|3")).isEqualTo(6);
        assertThat(calculator.add("//sep\n2sep5")).isEqualTo(7);
    }

    @Test
    void add_shouldThrowException_forUnexpectedDelimiter() {
        assertThatThrownBy(() -> calculator.add("//|\n1|2,3"))
                .isInstanceOf(CalculatorException.class)
                .hasMessageContaining("'|' expected but ',' found at position 3.");
    }

    @Test
    void add_shouldThrowException_forNegativeNumber() {
        assertThatThrownBy(() -> calculator.add("1,-2"))
                .isInstanceOf(CalculatorException.class)
                .hasMessage("Negative number(s) not allowed: -2");
    }

    @Test
    void add_shouldThrowException_forMultipleNegatives() {
        assertThatThrownBy(() -> calculator.add("2,-4,-9"))
                .isInstanceOf(CalculatorException.class)
                .hasMessage("Negative number(s) not allowed: -4, -9");
    }

    @Test
    void add_shouldIgnoreNumbersGreaterThan1000() {
        assertThat(calculator.add("2,1001")).isEqualTo(2);
        assertThat(calculator.add("2,1000")).isEqualTo(1002);
    }
}
