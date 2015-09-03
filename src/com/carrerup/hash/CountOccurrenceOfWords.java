package com.carrerup.hash;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

/*
 * ͳ��һƪ������ �����У��������ʳ��ֵĴ������㷨����HashMap ����ų��ֵĵ��ʵĴ�����Key ��Ҫͳ�Ƶĵ��ʣ�Value �ǵ��ʳ��ֵĴ�����
 ����ٰ��� Key ���������г�����
 */
public class CountOccurrenceOfWords {
	public static void main(String[] args) throws Exception {
		Map hashMap = null;
		BufferedReader infile = null;
		StringTokenizer st = null;
		String filename = "Test.txt";
		String string;
		String file = null;
		// ��һƪ���£������� Test.txt .
		infile = new BufferedReader(new FileReader(filename));
		while ((string = infile.readLine()) != null) {
			file += string; // ������ƪ���£�����String�С�
		}
		hashMap = new HashMap();
		// ȡ�������еĵ��ʣ�"," "." "!" " " Ϊ�������ʵķֽ����
		st = new StringTokenizer(file, " ,.!");
		while (st.hasMoreTokens()) {
			String key = st.nextToken();
			if (hashMap.get(key) != null) {
				int value = ((Integer) hashMap.get(key)).intValue();
				value++;
				hashMap.put(key, new Integer(value));
			} else {
				hashMap.put(key, new Integer(1));
			}
		}
		// ���յ��ʵ���ĸ���������
		Map treeMap = new TreeMap(hashMap);
		Set entrySet = treeMap.entrySet();
		Iterator iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}
}
