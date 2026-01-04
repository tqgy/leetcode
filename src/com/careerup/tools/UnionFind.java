package com.carrerup.diagram;

import java.util.*;

public class UnionFind {
    private Map<Integer, Integer> parent = new HashMap<>();
    private int numComponents = 0;

    public int find(int x) {
        parent.computeIfAbsent(x, k -> {
            numComponents++;
            return k;
        });
        if (parent.get(x) != x) {
            parent.put(x, find(parent.get(x)));
        }
        return parent.get(x);
    } 

    public void union(int x, int y) {
        int px = find(x);
        int py = find(y);
        if (px != py) {
            parent.put(px, py);
            numComponents--;
        }
    }

    public int count() {
        return numComponents;
    }

    public static void main(String[] args) {
        UnionFind uf = new UnionFind();
        uf.union(1, 2);
        uf.union(2, 3);
        System.out.println(uf.count());
        System.out.println(uf.find(1));
        System.out.println(uf.find(2));
        System.out.println(uf.find(3));
        System.out.println(uf.count());
        System.out.println(uf.find(4));
        uf.union(4, 5);
        System.out.println(uf.count());
    }
}
