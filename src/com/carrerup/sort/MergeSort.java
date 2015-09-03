package com.carrerup.sort;

/** 
 * 《合并排序�?，利用分治�?想进行排序�?（针对习�?.3-2�?
 * 《算法导论�?原文摘要�?
 * The  merge sort algorithm closely follows the divide -and-conquer paradigm . Intuitively, it  
 *  operates as follows.  
 * �?  Divide: Divide the  n-element sequence to be sorted into two subsequences of  n/2  
 *     elements each.  
 * �?  Conquer: Sort the two subsequences recursively using merge sort.  
 * �?  Combine: Merge the two sorted subsequences to produce the sorted answer.  
 * The recursion "bottoms out" when the sequence to be sorted has length 1,  in which case there  
 * is no work to be done, since every sequence  of length 1 is already in sorted order.  
 * The key operation of the merge sort algorithm is  the merging of two sorted sequences in the  
 * "combine" step. To perform the merging,  we use an auxiliary procedure MERGE( A,  p,  q,  r ),  
 * where A is an array and  p,  q, and r  are indices numbering elements of the array such that  p �? q <  r . 
 * The procedure assumes that the subarrays  A[p  q] and A[q + 1   r ] are in sorted order.  
 * It  merges  them to form a single sorted subarray that replaces the current subarray  A[p  r].  
 *  
 * Our MERGE procedure takes time Θ ( n), where n = r - p + 1 is the number of 
 * elements being merged, and it works as follows. Returning to our card-playing 
 * motif , suppose we have two piles of cards face up on a table. Each pile is 
 * sorted, with the smallest cards on top. We wish to merge the two piles into a 
 * single sorted output pile, which is to be face down on the table. Our basic 
 * step consists of choosing the smaller of the two cards on top of the face-up 
 * piles, removing it from its pile (which exposes a new top card), and placing 
 * this card face down onto the output pile. We repeat this step until one input 
 * pile is empty, at which time we just take the remaining input pile and place 
 * it f ace down onto the output pile. Computationally, each basic step takes 
 * constant time, since we are checking just two top cards. Since we perform at 
 * most n basic steps, merging takes Θ( n) time. 
 *  
 * 伪代码： 
 *  MERGE(A, p, q, r)  
 *1  n1  �?q -  p + 1  
 *2  n2  �?r -  q  
 *3  create arrays  L[1   n1  + 1] and  R[1   n2  + 1]  
 *4  for  i �?1  to n1   
 *5       do L[i] �?A[p +  i - 1]  
 *6  for  j �?1  to n2   
 *7       do R[j] �?A[q +  j]  
 *8  L[n1  + 1] �?�?（∞代表到达�?��值，哨兵位） 
 *9  R[n2  + 1] �?�? 
 *10  i �?1  
 *11  j �?1  
 *12  for  k �?p to r  
 *13       do if  L[i] �?R[j]  
 *14              then A[k] �?L[i]  
 *15                   i �?i + 1  
 *16              else A[k] �?R[j]  
 *17                   j �?j + 1  
 *@author guyu
 */ 
public class MergeSort { 
     
    private static int[] input = new int[] { 2, 1, 5, 4, 9, 8, 6, 7, 10, 3 }; 
 
    /** 
     * @param args 
     */ 
    public static void main(String[] args) { 
        mergeSort(input); 
        //打印数组 
        printArray(); 
    } 
     
    /** 
     * 针对习题2.3-2改写，与伪代码不对应 
     * @param array 
     * @return 
     */ 
    private static int[] mergeSort(int[] array) { 
        //如果数组的长度大�?，继续分解数�?
        if (array.length > 1) { 
            int leftLength = array.length / 2; 
            int rightLength = array.length - leftLength; 
            //创建两个新的数组 
            int[] left = new int[leftLength]; 
            int[] right = new int[rightLength]; 
            //将array中的值分别对应复制到两个子数组中 
            for (int i=0; i<leftLength; i++) { 
                left[i] = array[i]; 
            } 
            for (int i=0; i<rightLength; i++) { 
                right[i] = array[leftLength+i]; 
            } 
            //递归利用合并排序，排序子数组 
            left = mergeSort(left); 
            right = mergeSort(right); 
            //设置初始索引 
            int i = 0; 
            int j = 0; 
            for (int k=0; k<array.length; k++) { 
                //如果左边数据索引到达边界则取右边的�? 
                if (i == leftLength && j < rightLength) { 
                    array[k] = right[j]; 
                    j++; 
                //如果右边数组索引到达边界，取左数组的�?
                } else if (i < leftLength && j == rightLength) { 
                    array[k] = left[i]; 
                    i++; 
                //如果均为到达则取，较小的�?
                } else if (i < leftLength && j < rightLength) { 
                    if (left[i] > right[j]) { 
                        array[k] = right[j]; 
                        j++; 
                    } else { 
                        array[k] = left[i]; 
                        i++; 
                    } 
                }  
            } 
        }  
        return array; 
        /* 
         * 复杂度分析： 
         * 由于采用了�?归，设解决长度为n的数组需要的时间为T(n)，则分解成两个长度为n/2的子 
         * 数组后，�?��的时间为T(n/2)，合并需要时间�?n)。所以有当n>1时，T(n)=2T(n/2)+Θ(n), 
         * 当n=1时，T(1)=Θ(1) 
         * 解这个�?归式，设Θ(1)=c,(c为常�?，则Θ(n)=cn�?
         * 有上式可得T(n/2)=2T(n/4)+Θ(n/2),T(n/4)=2T(n/8)+Θ(n/4)....依次带入可得 
         * �?��可以有T(n)=nT(1)+Θ(n)+2Θ(n/2)+4Θ(n/4)...+(2^lgn)Θ(n/(2^lgn)),其中共有lgn个�?n)相加�?
         * 即T(n)=cn+cnlgn 
         * �?��，时间复杂度为：Θ(nlgn) 
         */ 
    } 
     
    private static void printArray() { 
        for (int i : input) { 
            System.out.print(i + " "); 
        } 
    } 
} 
