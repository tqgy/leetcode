package com.carrerup.pratice;

import java.util.*;

public class Calculator {
    public int calculate(String s) {
        // Convert the string to a list for easier manipulation
        List<Character> list = new ArrayList<>();
        for (char c : s.toCharArray()) {
            list.add(c);
        }
        return helper(list);
    }

    private int helper(List<Character> s) {
        Stack<Integer> stack = new Stack<>();
        char sign = '+';
        int num = 0;

        while (!s.isEmpty()) {
            char c = s.remove(0);

            // If the character is a digit, update the current number
            if (Character.isDigit(c)) {
                num = 10 * num + (c - '0');
            }

            // If the character is '(', recursively calculate the value inside the parentheses
            if (c == '(') {
                num = helper(s);
            }

            // If the character is an operator or the end of the string, process the current number
            if ((!Character.isDigit(c) && c != ' ') || s.isEmpty()) {
                if (sign == '+') {
                    stack.push(num);
                } else if (sign == '-') {
                    stack.push(-num);
                } else if (sign == '*') {
                    stack.push(stack.pop() * num);
                } else if (sign == '/') {
                    stack.push(stack.pop() / num);
                }
                num = 0;
                sign = c;
            }

            // If the character is ')', break out of the loop and return the result
            if (c == ')') {
                break;
            }
        }

        // Sum all values in the stack
        int result = 0;
        for (int value : stack) {
            result += value;
        }
        return result;
    }

    // Main function for testing
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        String input = "2*(5+3)+6-(3+2)*2";
        System.out.println(calculator.calculate(input)); // Output: 16
    }
}
