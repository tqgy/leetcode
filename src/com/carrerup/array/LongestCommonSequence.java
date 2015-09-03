package com.carrerup.array;

public class LongestCommonSequence {
	public static void main(String[] args) {
		String[] x = { "", "A", "A", "B", "C", "B", "D", "A", "B" };
		String[] y = {"", "A", "B", "D", "C", "A", "B", "A" };
		int[][] b = getLength(x, y);
		Display(b, x, x.length - 1, y.length - 1);

        String a = "blog.csdn.net";  
        String s = "csadan.blogt";  
        comSequence(a, s);  
    }  
  
    private static void comSequence(String str1, String str2) {  
        char[] a = str1.toCharArray();  
        char[] b = str2.toCharArray();  
        int a_length = a.length;  
        int b_length = b.length;  
        int[][] lcs = new int[a_length + 1][b_length + 1];  
        // ��ʼ������  
        for (int i = 0; i <= b_length; i++) {  
            for (int j = 0; j <= a_length; j++) {  
                lcs[j][i] = 0;  
            }  
        }  
        //Start from 1 not 0
        for (int i = 1; i <= a_length; i++) {  
            for (int j = 1; j <= b_length; j++) {  
                if (a[i - 1] == b[j - 1]) {  
                    lcs[i][j] = lcs[i - 1][j - 1] + 1;  
                } else {  
                    lcs[i][j] = lcs[i][j - 1] > lcs[i - 1][j] ? lcs[i][j - 1] : lcs[i - 1][j];  
                }  
            }  
        }  
        // �����������й۲�  
        for (int i = 0; i <= a_length; i++) {  
            for (int j = 0; j <= b_length; j++) {  
                System.out.print(lcs[i][j]+",");  
            }  
            System.out.println("");  
        }  
        // �����鹹����С�����ַ�  
        int max_length = lcs[a_length][b_length];  
        char[] comStr = new char[max_length];  
        int i =a_length, j =b_length;  
        while(max_length>0){  
            if(lcs[i][j]!=lcs[i-1][j-1]){  
                if(lcs[i-1][j]==lcs[i][j-1]){//���ַ���ȣ�Ϊ�����ַ�? 
                    comStr[max_length-1]=a[i-1];  
                    max_length--;  
                    i--;j--;  
                }else{//ȡ�����нϳ�����ΪA��B�������������  
                    if(lcs[i-1][j]>lcs[i][j-1]){  
                        i--;  
                    }else{  
                        j--;  
                    }  
                }  
            }else{  
                i--;j--;  
            }  
        }  
        System.out.print(comStr);  
    }  
    
//	public static int[][] buildMatrix(String s1, String s2){
//		char[] x = s1.toCharArray();
//		char[] y = s2.toCharArray();
//		int[][] t = new int[x.length][y.length];
//		int[][] r = new int[x.length][y.length];
//		for(int i = 0; i < x.length; i++){
//			
//		}
//	}

	public static int[][] getLength(String[] x, String[] y) {
		int[][] b = new int[x.length][y.length];
		int[][] c = new int[x.length][y.length];
		for (int i = 1; i < x.length; i++) {
			for (int j = 1; j < y.length; j++) {
				if (x[i] == y[j]) {
					c[i][j] = c[i - 1][j - 1] + 1;
					b[i][j] = 1;
				} else if (c[i - 1][j] >= c[i][j - 1]) {
					c[i][j] = c[i - 1][j];
					b[i][j] = 0;
				} else {
					c[i][j] = c[i][j - 1];
					b[i][j] = -1;
				}
			}
		}
		return b;
	}

	public static void Display(int[][] b, String[] x, int i, int j) {
		if (i == 0 || j == 0)
			return;
		if (b[i][j] == 1) {
			Display(b, x, i - 1, j - 1);
//			System.out.print(x[i] + " ");
		} else if (b[i][j] == 0) {
			Display(b, x, i - 1, j);
		} else if (b[i][j] == -1) {
			Display(b, x, i, j - 1);
		}
	}
}