package com.carrerup.pratice;

public class Solution2 {

	public static int[] toBinary(int value) {
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
//		StringBuilder s = new StringBuilder();
//		for (int k = array.length - 1; k >= 0; k--){
//			System.out.print(array[k]);
//			s.append(array[k]);
//			
//		}
//		System.out.print("\t");
		return array;
	}



	static int getIntegerComplement(int n) {
		
	    if(n<=0 && n>=100000){
	        System.out.println("Error! Input out of scope!");
	        return 0;
	    }
	    int[] a = toBinary(n);
	    //Reverse the string
	    for (int i = 0; i < a.length; i++) {
			System.out.print(a[i]);
		}
	    System.out.println();
	    for (int i = 0; i < a.length; i++) {
	        if(0 == a[i]){
	            a[i] = 1;
	        }else if(1 == a[i]){
	            a[i] = 0;
	        }else{
	            System.out.println("Error");
	        }
	    }
	    for (int i = 0; i < a.length; i++) {
			System.out.print(a[i]);
		}
	    System.out.println();
	    //Change back to Decimal
	    int value = toDecimal(a);

	    return value;
	}



	private static int toDecimal(int[] a){
				
	    int result = 0;
	    int num = 0;
	    for (int i = a.length - 1; 0 <= i; i--) {
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
	        int sum = a[i];
	        result = result + (sum * temp);
	        num++;
	    }
	    return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(getIntegerComplement(50));
	}

}
