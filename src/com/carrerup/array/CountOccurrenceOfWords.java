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
 * 统计一篇给定的 文章中，各个单词出现的次数的算法。用HashMap 来存放出现的单词的次数，Key 是要统计的单词，Value 是单词出现的次数。
 最后再按照 Key 的升序排列出来。
 */
public class CountOccurrenceOfWords {
	public static void main(String[] args) throws Exception {
		Map hashMap = null;
		BufferedReader infile = null;
		StringTokenizer st = null;
		String filename = "Test.txt";
		String string;
		String file = null;
		// 打开一篇文章，名字是 Test.txt .
		infile = new BufferedReader(new FileReader(filename));
		while ((string = infile.readLine()) != null) {
			file += string; // 都出整篇文章，存入String中。
		}
		hashMap = new HashMap();
		// 取出文章中的单词，"," "." "!" " " 为各个单词的分界符。
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
		// 按照单词的字母次序输出。
		Map treeMap = new TreeMap(hashMap);
		Set entrySet = treeMap.entrySet();
		Iterator iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}
}
