package service;

import exception.CalculatorException;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    public int add(String input) {
        if (input == null || input.isEmpty()) return 0;

        String delimiter = null;
        String numbers = input;
        String customDelimiterPrefix = "//";
        char newLine = '\n';

        if (input.startsWith(customDelimiterPrefix)) {
            int newlineIndex = input.indexOf(newLine);
            if (newlineIndex == -1)
                throw new CalculatorException("Missing newline after delimiter");

            delimiter = input.substring(2, newlineIndex);
            if (delimiter.isEmpty())
                throw new CalculatorException("Delimiter cannot be empty");

            numbers = input.substring(newlineIndex + 1);
        }

        List<String> errors = new ArrayList<>();
        List<Integer> negatives = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();
        int sum = 0;
        int currentPosition = 0;

        while (currentPosition < numbers.length()) {
            boolean matchedDelimiter = false;

            if (delimiter != null && numbers.startsWith(delimiter, currentPosition)) {
                if (currentNumber.isEmpty()) {
                    errors.add("Missing number before delimiter at currentPosition " + currentPosition + ".");
                } else {
                    sum += processNumber(currentNumber.toString(), negatives, errors);
                    currentNumber.setLength(0);
                }
                currentPosition += delimiter.length();
                matchedDelimiter = true;
            } else if (delimiter == null) {
                char c = numbers.charAt(currentPosition);
                if (c == ',' || c == newLine) {
                    if (currentNumber.isEmpty()) {
                        errors.add("Missing number before delimiter at position " + currentPosition + ".");
                    } else {
                        sum += processNumber(currentNumber.toString(), negatives, errors);
                        currentNumber.setLength(0);
                    }
                    currentPosition++;
                    matchedDelimiter = true;
                }
            }

            if (!matchedDelimiter) {
                if (delimiter != null) {
                    char c = numbers.charAt(currentPosition);
                    if (c == ',' || c == newLine) {
                        errors.add("'" + delimiter + "' expected but '" + c + "' found at position " + currentPosition + ".");
                    }
                }
                currentNumber.append(numbers.charAt(currentPosition));
                currentPosition++;
            }
        }

        if (!currentNumber.isEmpty()) {
            sum += processNumber(currentNumber.toString(), negatives, errors);
        } else {
            errors.add("Separator at the end is not allowed");
        }

        if (!negatives.isEmpty()) {
            errors.add("Negative number(s) not allowed: " + negatives.toString().replaceAll("[\\[\\]]", ""));
        }

        if (!errors.isEmpty()) {
            throw new CalculatorException(String.join("\n", errors));
        }

        return sum;
    }

    private int processNumber(String token, List<Integer> negatives, List<String> errors) {
        try {
            int num = Integer.parseInt(token);
            if (num < 0) negatives.add(num);
            return num <= 1000 ? num : 0;
        } catch (NumberFormatException e) {
            errors.add("Invalid number: " + token);
            return 0;
        }
    }
}
