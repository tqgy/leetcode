/**
 * 
 */
package com.carrerup.sort;

/** 
 *  ���������� ����������Ԫ�ؽ����������Ч�㷨�� 
 *  ���㷨���ۡ�ԭ��ժҪ�� 
 * Insertion sort works the way many people sort a hand 
 * of playing cards. We start with an empty left hand and the cards face down on 
 * the table. We then remove one card at a time from the table and insert it 
 * into the correct position in the left hand. To find the correct position for 
 * a card, we compare it with each of the cards already in the hand, from right 
 * to left, as illustrated in Figure 2.1. At all times, the cards held in the 
 * left hand are sorted, and these cards were originally the top cards of the 
 * pile on the table.  
 * α�������£�  
 * for j<- 2 to length[A]  
 *  do key <- A[j]  
 *      >Insert A[j] into the sorted sequence A[1..j-1] ��>���Ŵ������Ĳ�����ע�ͣ� 
 *      i <- j-1  
 *      while i>0 and A[i]>key  
 *          do A[i+1] <- A[i]  
 *          i <- i-1  
 *      A[i+1] <- key  
 * �㷨���Ӷȣ�n^2 
 * @author guyu
 * 
 */ 
public class InsertionSort { 
 
    private static int[] input = new int[] { 2, 1, 5, 4, 9, 8, 6, 7, 10, 3 }; 
 
    /** 
     * @param args 
     */ 
    public static void main(String[] args) { 
        //������ڶ���Ԫ�ؿ�ʼ������Ϊ��һ��Ԫ�ر���϶����Ѿ��ź���� 
        for (int j = 1; j < input.length; j++) {// ���Ӷ� n 
            int key = input[j]; 
            int i = j - 1; 
            //���θ�֮ǰ��Ԫ�ؽ��бȽϣ�������ֱ�ǰ���ԭ��С���򽻻�λ�ã������������ 
            while (i >= 0 && input[i] > key) {//���Ӷȣ�1+2+...+(n-1)=��(n^2) 
                input[i + 1] = input[i]; 
                input[i] = key; 
                i--; 
            } 
        } 
        /* 
         * �������ո��Ӷ�Ϊn*n=n^2����������£������Ѿ����кõ�����£����ڶ���n=1�� ��������������£����Ӷ�Ϊn�� 
         */ 
        // ��ӡ���� 
        printArray(); 
    } 
 
    private static void printArray() { 
        for (int i : input) { 
            System.out.print(i + " "); 
        } 
    } 
} 