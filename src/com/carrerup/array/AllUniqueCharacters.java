/**
 * 
 */
package com.carrerup.array;

/**
 * @author guyu
 *
 */
public class AllUniqueCharacters {

	public static boolean isAllUniqueChars(char[] chars){

		if(chars == null ){
			return false;
		}
		
		if(chars.length == 0 ){
			return false;
		}
		
		boolean[] hit = new boolean[256];
		for(int i = 0; i < hit.length ; i++){
			hit[i] = false;
		}

		for(int i = 0; i < chars.length ; i++){
			if(hit[chars[i]] == false){
				hit[chars[i]] = true;
			}else{
				return false;
			}
		}
		return true;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Test cases for differernt inputs and verify the output
		System.out.println(isAllUniqueChars(null));
		System.out.println(isAllUniqueChars(new char[]{}));
		System.out.println(isAllUniqueChars(new char[]{'a', 'b', 'c'}));
		System.out.println(isAllUniqueChars(new char[]{'a', 'b', 'a'}));
		System.out.println(isAllUniqueChars(new char[]{'1', '2', '3', '4'}));
		System.out.println(isAllUniqueChars(new char[]{'1', '2', '3', '1'}));
	}

}
