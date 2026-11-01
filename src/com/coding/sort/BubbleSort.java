/**
 * 
 */
package com.carrerup.sort;

/** 
 * “冒泡排序�?,《算法导论�?思�?�?-2 
 * 伪代码： 
 * BUBBLESORT(A) 
 * 1 for i �?1 to length[A] 
 * 2 do for j �?length[A] downto i + 1 
 * 3 do if A[j] < A[j - 1] 
 * 4 then exchange A[j] �?A[j - 1]  
 * @author guyu 
 */ 
public class BubbleSort { 
     
    private static int[] input = new int[] { 2, 5, 1, 4, 9, 8, 10, 7, 6, 3 }; 
 
    public static void main(String[] args) { 
        int endIndex = input.length - 1; 
        for (int i = 0; i < endIndex; i++) {//复杂度：n 
            for (int j = endIndex; j > i; j--) {//复杂度：1+2+...+(n-1)=Θ(n^2) 
                if (input[j] < input[j-1]) { 
                    int temp = input[j]; 
                    input[j] = input[j-1]; 
                    input[j-1] = temp; 
                } 
            } 
        } 
        /* 
         * 复杂度分析： 
         * 由上可见冒泡排序的复杂度为：Θ(n^2)。在�?��情况下，虽无�?��换，但比较的次数仍为：�?n^2)�?
         * 在最佳情况下，复杂度也为Θ(n^2)，此时不如插入排序�? 
         * 冒泡排序的交换次数也大于插入排序�?
         * 冒泡排序对n个项目需要O(n2)的比较次数，且可以原地排序�? 
         * 尽管这个算法是最�?��了解和实作的排序算法之一，但它对于少数元素之外的数列排序是很没有效率的�? 
         * 冒泡排序是稳定的�?
         */ 
        //打印数组 
        printArray(); 
    } 
     
    private static void printArray() { 
        for (int i : input) { 
            System.out.print(i + " "); 
        } 
    } 
} 