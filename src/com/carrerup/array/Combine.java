package com.carrerup.array;

public class Combine {

	String r = "";
	String s = "12345";
	char[] t = s.toCharArray();

	void f(int n) {
		if (n == 0){
			System.out.println(r);	
		}else {
			for (int i = 0; i <= n - 1; i++) {
				String temp1 = r;
				char temp = t[n - 1];
				r += t[i];
				t[n - 1] = t[i];
				t[i] = temp;
				f(n - 1);
				t[i] = t[n - 1];
				t[n - 1] = temp;
				r = temp1;
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Combine c = new Combine();
		c.f(5);

	}

}
