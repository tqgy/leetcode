package com.carrerup.sort;

/** 
 * ��ѡ�����򡷣����㷨���ۡ�ϰ��2.2-2  
 * Consider sorting n numbers stored in array A by first 
 * finding the smallest element of A and exchanging it with the element in A[1]. 
 * Then find the second smallest element of A, and exchange it with A[2]. 
 * Continue in this manner for the first n - 1 elements of A. Write pseudocode 
 * for this algorithm, which is known as selection sort . What loop invariant 
 * does this algorithm maintain? Why does it need to run for only the first n - 
 * 1 elements, rather than for all n elements? Give the best-case and worst- 
 * case running times of selection sort in ��- notation.  
 * α���룺 
 *  for i <- 1 to length[A]-1 
 *      key <- A[i] 
 *      index <- i 
 *      for j <- 2 to length[A] 
 *          key = key < A[j] ? key : A[j]; 
            index = key < A[j] ? index : j; 
 *      A[index] = A[i]; 
        A[i] = key;  
 * @author guyu
 */ 
public class SelectionSort { 
     
    private static int[] input = new int[] { 2, 1, 5, 4, 9, 8, 6, 7, 10, 3 }; 
 
    /** 
     * @param args 
     */ 
    public static void main(String[] args) { 
        for (int i=0; i<input.length-1; i++) {//���Ӷ���������n 
            int key = input[i]; 
            int index = i; 
            //�Ƚϵ�ǰֵ����һ��ֵ�Ĺ�ϵ����¼�½�Сֵ��ֵ�������������ڽ����� 
            for (int j=i+1; j<input.length; j++) {//���Ӷȣ�1+2+...+(n-1)=��(n^2)
                key = key < input[j] ? key : input[j]; 
                index = key < input[j] ? index : j; 
            } 
            input[index] = input[i]; 
            input[i] = key; 
        } 
        /* 
         * ���Ӷȷ����� 
         * �����£����Ӷ�Ϊ��n*n=n^2(����΢��ȷ�ļ��㼴Ϊ��n-1+1+2+...+n-1=(2+n)*(n-1)/2, 
         * ���Ը��Ӷ���Ϊn^2�� 
         * ��������£����ڲ���ԭ�����Ƿ�����ã�����Ҫȫ��������ȷ����ǰ����Сֵ�����Ը��ӶȲ�����δn^2�� 
         *  
         */ 
        //��ӡ���� 
        printArray(); 
    } 
	private static int toDecimal1(char[] cars){
		
		int result = 0;
		int num = 0;
		for (int i = cars.length - 1; 0 <= i; i--) {
			int temp = 2;
			if (num == 0) {
				temp = 1;
			} else if (num == 1) {
				temp = 2;
			} else {
				for (int j = 1; j < num; j++) {
					temp = temp * 2;
				}
			}
			int sum = Integer.parseInt(String.valueOf(cars[i]));
			result = result + (sum * temp);
			num++;
		}
		return result;
	}
	private static String toBinary1(int value) {
	    int count = 1;
	    int m = 2;
	    while (value >= m) {
	        ++count;
	        m *= 2;
	    }
	    
	    int array[] = new int[count];
	    int i = 0;
	    while (value != 0) {
	        array[i] = value % 2;
	        value /= 2;
	        i++;
	    }
	    
	    StringBuilder s = new StringBuilder();
	    for (int k = array.length - 1; k >= 0; k--){
	        s.append(array[k]);
	    }

	    return s.toString();
	}
    private static void printArray() { 
        for (int i : input) { 
            System.out.print(i + " "); 
        } 
    } 
 
} 