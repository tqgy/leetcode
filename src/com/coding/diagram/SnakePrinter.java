package com.carrerup.diagram;

public class SnakePrinter {  
    
    private boolean error;  
    private Direction direction=Direction.RIGHT;  
    private int[][] matrix;  
      
    public static void main(String[] args) {  
        int n=7;  
        SnakePrinter snake=new SnakePrinter();  
        snake.initial(n);  
        if(!snake.error){  
            snake.print();  
            //System.out.println(Arrays.deepToString(snake.matrix));  
        }  
    }  
  
    public void initial(int n){  
        if(n<0)return;  
        matrix=new int[n][n];  
        int row=0,col=0;  
        int value=1;  
        int max=n*n;  
        while(value<=max){  
            matrix[row][col]=value;  
            direction=findDirection(row,col,n);  
            switch(direction){  
            case RIGHT:  
                col++;  
                break;  
            case LEFT:  
                col--;  
                break;  
            case DOWN:  
                row++;  
                break;  
            case UP:  
                row--;  
                break;  
            default:  
                this.error=true;   
                break;  
            }  
            value++;  
        }  
    }  
      
    public Direction findDirection(int row,int col,int len){  
        if(row<0||col<0||row>=len||col>=len){  
            this.error=true;  
        }  
        Direction direction=this.direction;  
        switch(direction){  
        case RIGHT:  
            if(col==len-1 || matrix[row][col+1]!=0){  
                direction=Direction.DOWN;  
            }  
            break;  
        case LEFT:  
            if(col==0 || matrix[row][col-1]!=0){  
                direction=Direction.UP;  
            }  
            break;  
        case DOWN:  
            if(row==len-1 || matrix[row+1][col]!=0){  
                direction=Direction.LEFT;  
            }  
            break;  
        case UP:  
            if(row==0|| matrix[row-1][col]!=0){  
                direction=Direction.RIGHT;  
            }  
            break;  
        default:  
            this.error=true;   
            break;  
        }  
        return direction;  
    }  
    public void print(){  
        if(matrix==null){  
            return;  
        }  
        int len=matrix.length;  
        for(int row=0;row<len;row++){  
            for(int col=0;col<len;col++){  
                System.out.print(matrix[row][col]+" ");  
            }  
            System.out.println();  
        }  
    }  
    public enum Direction{  
        RIGHT,DOWN,LEFT,UP  
    }  
}  