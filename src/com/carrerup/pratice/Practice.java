package com.carrerup.pratice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Practice {

	static class TreeNode {
		int value;
		TreeNode left;
		TreeNode right;
	}

	public static int hammingDistance(int x, int y) {
			int cnt = 0;
			while(x != 0 || y != 0){
				int xa = x & 1;
				int ya = y & 1;
				if(xa != ya){
					cnt++;
				}
				x = x>>1;
				y = y>>1;   
			}
			return cnt;
		}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TreeNode root1 = new TreeNode();
		root1.value = 1;
		TreeNode root3 = new TreeNode();
		root3.value = 3;
		TreeNode root2 = new TreeNode();
		root2.value = 2;
		TreeNode root5 = new TreeNode();
		root5.value = 5;
		root1.left = root2;
		root1.right = root3;
		root2.left = root5;
		System.out.println(widthOfBinaryTree(root1));
		Map<Character, Integer> indexs = new HashMap<>();
		TreeSet<String> topkSet = new TreeSet<>();
		topkSet.pollFirst();
		topkSet.pollLast();

		Iterator<String> it = topkSet.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
		// iterate the indexs and print the values
		for (Map.Entry<Character, Integer> entry : indexs.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
        char c = 'a';
		Character.isLetter(c);
		PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> b[1] - a[1]);
		String s1 = String.valueOf('a');
		s1 = String.valueOf(new char[]{'a', 'b'});
		Set<Character> set = new HashSet<>(Arrays.asList('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U'));
		StringBuilder prefix = new StringBuilder();
		prefix.append(c);                         // 添加
        prefix.deleteCharAt(prefix.length() - 1); // 删除
		List<Integer> values = new ArrayList<>();
		values.add(1);
		values.remove(values.size() - 1);
		values.sort((a, b) -> a - b);
		System.out.println(hammingDistance(1, 4));
		int start = -6;
		int end = -2;
		for (int i = start; i < end; i += 5) {
			// System.out.println(i);
			System.out.println(i + "~" + (i + 5 > end ? end : i + 5));
		}
		List<Integer> res = new ArrayList<>();
		Queue<TreeNode> queue = new LinkedList<>();
		Stack<Integer> stack = new Stack<>();
        ArrayList<TreeNode> tQueue = new ArrayList<>();
		tQueue.add(new TreeNode());
		tQueue.removeIf(a -> a.value == 0);
		tQueue.stream().anyMatch(a->a.value == 1);
		int[] arr = new int[10];
		String s = "adsf@dfs@sdf@";
		String[] a = s.split("@");
		System.out.println("length : " + a.length);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
		TreeMap<Integer, Integer> treeMap = new TreeMap<>();
		treeMap.higherKey(null);
		System.out.println("---- : " + treeMap.floorKey(1));
		treeMap.put(3, 2);
		treeMap.put(5, 4);
		System.out.println("---- : " + treeMap.floorKey(1));
		System.out.println("---- : " + treeMap.floorKey(4));

		TreeSet<Integer> treeSet = new TreeSet<>();
		treeSet.add(3);
		System.out.println("~~~ : " + treeSet.floor(1));
		System.out.println("~~~ : " + treeSet.floor(4));
		
		for(Map.Entry<Integer, Integer> entry : treeMap.entrySet()){
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}

		treeMap.remove(3);
		for(Map.Entry<Integer, Integer> entry : treeMap.entrySet()){
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}

	
	}

	private static class Account {
        private String accountId;

        public int getBalance() {
            return balance;
        }

        private int balance;
        public Account(String accountId){
            this.accountId = accountId;
            this.balance = 0;
        }
    }
    private Map<String, Account> map;

	public List<String> topActivity(int n){
        return map.values().stream().sorted((a, b) -> {
            if (a.balance == b.balance){
                return a.accountId.compareTo(b.accountId);
            } else {
                return b.balance - a.balance;
            }
		}).limit(n).map(a -> a.getBalance()+"").collect(Collectors.toList());
    }

public static int widthOfBinaryTree(TreeNode root) {
        // bfs, null node also add as null node, calculate each level, first non null node and last non null node
        int max = 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while(!queue.isEmpty()){
            int size = queue.size();
            ArrayList<TreeNode> tQueue = new ArrayList<>();
            boolean nonNull = false;
            for(int i = 0; i < size; i++){
                TreeNode cur = queue.poll();
                if(cur != null){
                    nonNull = true;
                    tQueue.add(cur.left);
                    tQueue.add(cur.right);
                }else{
                    tQueue.add(null);
                    tQueue.add(null);
                }
            }
            if(!nonNull) 
                return max;
            int l = 0, r = tQueue.size() - 1;
            while(tQueue.get(l) == null && l < r) l++;
            while(tQueue.get(r) == null && l < r) r--;
            
            max = Math.max(max, r - l + 1);

            for(int i = 0; i < tQueue.size(); i++){
                queue.offer(tQueue.get(i));
            }
        }
        return max;
    }


	public static void printLevel(TreeNode root) {
		if (root == null) {
			return;
		}
		LinkedList queue = new LinkedList();
		queue.add(root);
		TreeNode end = root;
		int num = 0;
		while (queue.size() != 0) {
			if (root.left != null) {
				queue.add(root.left);
			}
			if (root.right != null) {
				queue.add(root.right);
			}
			num++;
			root = (TreeNode) queue.poll();
			if (root == end) {
				System.out.println(num);
				end = (TreeNode) queue.getLast();
				num = 0;
			}

		}
	}
}
