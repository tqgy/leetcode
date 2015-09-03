package com.carrerup.array;

import java.util.ArrayList;  
import java.util.List;  
  
/*
 * 输入一个字符串，输出该字符串中字符的所有组合。举个例子，如果输入abc，它的组合有a、b、c、ab、ac、bc、abc
 */
public class Combination {  
  
    public static void main(String[] args) {  
  
        char[] a = { 'a', 'b', 'c' };  
        List<Character> list = new ArrayList<Character>();  
        for (int i = 1, len = a.length; i <= len; i++) {  
            combine(a, 0, i, list);  
        }  
    }  
        /* 
     * we can also use Stack like this: 
        stack.push(a[begin]); 
        combination(a,begin+1,number-1,stack); 
        stack.pop(); 
        combination(a,begin+1,number,stack); 
     */  
    public static void combine(char[] a, int begin, int resultLength,List<Character> list) {  
          
        if (resultLength == 0) {  
            System.out.println(list.toString());  
            return;  
        }  
        if (begin == a.length){  
            return;  
        }  
          
        list.add(a[begin]);  
        combine(a, begin + 1, resultLength - 1, list);  
        list.remove((Character) a[begin]);  
        combine(a, begin + 1, resultLength, list);  
    }  
}  