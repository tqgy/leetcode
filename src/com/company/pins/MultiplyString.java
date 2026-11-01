package com.careerup.pins;

public class MultiplyString {

    public static String multiply(String num1, String num2) {
        int len1 = num1.length(), len2 = num2.length();
        int[] result = new int[len1 + len2];

        for (int i = len1 - 1; i >= 0; i--) {
            for (int j = len2 - 1; j >= 0; j--) {
                int temp = (num1.charAt(i) - '0') * (num2.charAt(j) - '0');
                int high = i + j, low = i + j + 1;
                // result[low] act as carry
                int sum = temp + result[low];
                // add carry to result[high] using "+="
                result[high] += sum / 10;
                // record final number to result[low] using "="
                result[low] = (sum) % 10;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int p : result){
            if (sb.length() != 0 || p != 0){
                sb.append(p);
            }
        }
        return sb.length() == 0 ? "0" : sb.toString();
    }

    public static void main(String[] args) {
        String num1 = "23", num2 = "13";
        System.out.println(multiply(num1, num2));
    }
}
