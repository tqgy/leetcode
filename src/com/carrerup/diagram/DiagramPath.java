/**
 * 
 */
package com.carrerup.diagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @author guyu
 * 
 */
public class DiagramPath {

	private final static int WIDTH = 10;
    private final static int HEIGHT = 10;
    
    /**
     * gen a random a[m][n],elements from 1--100
     */
    private static int[][] initData(int m, int n){
    	int[][] a=new int[m][n];
        Random random=new Random();
        for(int i=0;i<m;i++) {
            for(int j=0;j<n;j++) {
                a[i][j]=random.nextInt(100)+1;
            }
        }
        //printArray(a);
        return a;
    }
    

 

    /**
     * print a[m][m] to stdout
     */
    public static void printArray(int[][] a) {
        for (int[] anA : a) {
            for (int anAnA : anA) {
                System.out.printf("%d\t", anAnA);
            }
            System.out.println();
        }
    }
    
    public static void printArray(boolean[][] a) {
        for (boolean[] anA : a) {
            for (boolean anAnA : anA) {
                System.out.printf("\t", anAnA);
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        int[][] numbers = initData(10, 10);
        
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        //Keep a current node's List
        List<Point> currList = new ArrayList<Point>();
        currList.add(new Point(0, 0, numbers[0][0]));
        visited[0][0] = true;
        
        while(true) {
            List<Point> tempList = new ArrayList<Point>();
            for (Point point : currList) {
                if (point.x > 0 && !visited[point.x - 1][point.y]) {
                    tempList.add(new Point(point.x - 1, point.y, 
                            point.value + numbers[point.x - 1][point.y]));
                }
                if (point.x < WIDTH - 1 && !visited[point.x + 1][point.y]) {
                    tempList.add(new Point(point.x + 1, point.y, 
                            point.value + numbers[point.x + 1][point.y]));
                }
                if (point.y > 0 && !visited[point.x][point.y - 1]) {
                    tempList.add(new Point(point.x, point.y - 1, 
                            point.value + numbers[point.x][point.y - 1]));
                }
                if (point.y < HEIGHT - 1 && !visited[point.x][point.y + 1]) {
                    tempList.add(new Point(point.x, point.y + 1, 
                            point.value + numbers[point.x][point.y + 1]));
                }
            }
            Point minPoint = new Point(0, 0, Integer.MAX_VALUE);
            for (Point point : tempList) {
                if (minPoint.value > point.value) {
                    minPoint = point;
                }
            }
            if (minPoint.x == WIDTH - 1 && minPoint.y == HEIGHT - 1) {
                System.out.println(minPoint.value);
                break;
            }
            visited[minPoint.x][minPoint.y] = true;
            currList.add(minPoint);
            for(Iterator<Point> iter = currList.iterator(); iter.hasNext();) {
                Point point = iter.next();
                if ((point.x == 0 || visited[point.x - 1][point.y])
                        && (point.x == WIDTH - 1 || visited[point.x + 1][point.y])
                        && (point.y == 0 || visited[point.x][point.y - 1])
                        && (point.y == HEIGHT - 1 || visited[point.x][point.y + 1])) {
                    iter.remove();
                }
            }
           
        }
    }

    private static class Point {
        int x;
        int y;
        int value;
        
        public Point(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }

}
