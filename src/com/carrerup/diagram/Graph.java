package com.carrerup.diagram;

import java.util.ArrayList;  
import java.util.LinkedList;  
import java.util.List;  
import java.util.Stack;  
  
public class Graph {  
  
  
    /**关键字：图 邻接表 深度优先遍历 广度优先遍历 最短路径 
     * key words:Graph Adjacent-list depthFirstVisit breadthFirstVisit getCheapestPath 
     * 1.Graph is a collection of Vertex and Edge. 
     * When you want to implement 'Graph',you deal with two missions:how to implement 'Vertex' and 'Edge' 
     *  a.Vertex: 
     *    Type label-->Vertex's ID,to separate one Vertex from another,like 'A','B',or 0,1,2 
     *    List<Edge> edgeList-->store edges that start with this Vertex. 
     *    ...<output omit> 
     *  b.Edge(as Inner class of Vertex): 
     *    endVertex-->(begin,end),the outer class is 'begin',endVertex is 'end' 
     *    ...<output omit> 
     * 2.In the following,we  
     *  a.use ArrayList to store Vertices 
     *  b.use 'char' as Vertex's ID 
    */  
    private List<Vertex> vertices;  
    private int edgeCount;  
    private List<Vertex> depthFirstResult;  
    private List<Vertex> breadthFirstResult;  
      
public void breadthFirstVisit(char beginLabel){  
          
        Vertex origin=this.getVertexByChar(beginLabel);  
        if(origin==null)return;  
        List<Vertex> queue=new LinkedList<Vertex>();  
        origin.setVisited(true);  
        queue.add(origin);  
        breadthFirstResult.add(origin);  
        Vertex curVertex=null;  
        while(!queue.isEmpty()){  
            curVertex=queue.remove(0);  
            while(curVertex.getFirstUnvisitedNeighbor()!=null){  
                Vertex tmpVertex=curVertex.getFirstUnvisitedNeighbor();  
                tmpVertex.setVisited(true);  
                queue.add(tmpVertex);  
                breadthFirstResult.add(tmpVertex);  
            }  
              
        }  
        printVertexList(breadthFirstResult);  
}  
      
    public void depthFirstVisit(char beginLabel){  
          
        Vertex origin=this.getVertexByChar(beginLabel);  
        if(origin==null)return;  
        Stack<Vertex> stack=new Stack<Vertex>();  
        origin.setVisited(true);  
        stack.push(origin);  
        depthFirstResult.add(origin);  
        Vertex curVertex=null;  
        while(!stack.isEmpty()){  
            curVertex=stack.peek();  
            Vertex tmpVertex=curVertex.getFirstUnvisitedNeighbor();  
            if(tmpVertex!=null){  
                tmpVertex.setVisited(true);  
                depthFirstResult.add(tmpVertex);  
                stack.push(tmpVertex);  
            }else{  
                stack.pop();  
            }  
        }  
        printVertexList(depthFirstResult);  
    }  
      
    //getShortestPath.Base on breadthFirstVisit.the edge has no weight.  
    public double getShortestPath(char beginLabel,char endLabel,Stack<Vertex> stack){  
        resetVertices();  
        Vertex begin=this.getVertexByChar(beginLabel);  
        Vertex end=this.getVertexByChar(endLabel);  
        begin.setVisited(true);  
        List<Vertex> queue=new LinkedList<Vertex>();  
        queue.add(begin);  
        boolean done=false;  
        while(!done&&!queue.isEmpty()){  
            Vertex curVertex=queue.remove(0);  
            while(!done&&curVertex.getFirstUnvisitedNeighbor()!=null){  
                Vertex tmpVertex=curVertex.getFirstUnvisitedNeighbor();  
                tmpVertex.setVisited(true);  
                double  tmpCost=curVertex.getCost()+1;  
                tmpVertex.setPreviousVertex(curVertex);  
                tmpVertex.setCost(tmpCost);  
                queue.add(tmpVertex);  
                if(tmpVertex.equals(end)){  
                    done=true;  
                }  
            }  
        }  
        double pathLength=end.getCost();  
        //find the path.traverse back from end  
        while(end!=null){  
            stack.push(end);  
            end=end.getPreviousVertex();  
        }  
        return pathLength;  
    }  
      
    public boolean addEdge(char beginLabel,char endLabel,double weight){  
        int beginIndex=vertices.indexOf(new Vertex(beginLabel));  
        int endIndex=vertices.indexOf(new Vertex(endLabel));  
        Vertex beginVertex=vertices.get(beginIndex);  
        Vertex endVertex=vertices.get(endIndex);  
        boolean result=beginVertex.connect(endVertex,weight);  
        edgeCount++;  
        return result;  
    }  
    public boolean addEdge(char beginLabel,char endLabel){  
        return addEdge(beginLabel,endLabel,0);  
    }  
    public boolean addVertex(char label){  
        boolean result=false;  
        Vertex newVertex=new Vertex(label);  
        if(!vertices.contains(newVertex)){  
            vertices.add(newVertex);//reject duplicate vertex  
            result=true;  
        }  
        return result;  
    }  
    public void printVertexList(List<Vertex> list){  
        for(int i=0,len=list.size();i<len;i++){  
            Vertex vertex=list.get(i);  
            System.out.print(vertex.getLabel()+" ");  
        }  
        System.out.println();  
    }  
      
    public void resetVertices(){  
        for(int i=0,len=vertices.size();i<len;i++){  
            Vertex vertex=vertices.get(i);  
            vertex.setPreviousVertex(null);  
            vertex.setVisited(false);  
            vertex.setCost(0);  
        }  
    }  
      
    public Vertex getVertexByChar(char target){  
        Vertex vertex=null;  
        for(int i=0,len=vertices.size();i<len;i++){  
            vertex=vertices.get(i);  
            Character xx=vertex.getLabel();  
            if(xx.charValue()==target){  
                return vertex;  
            }  
        }  
        return vertex;  
    }  
      
    public Graph(){  
        vertices=new ArrayList<Vertex>();  
        edgeCount=0;  
        depthFirstResult=new ArrayList<Vertex>();  
        breadthFirstResult=new ArrayList<Vertex>();  
    }  
  
      
      
    public static void main(String[] args) {  
          
        Graph graph=createGapth();  
        graph.depthFirstVisit('7');//depthFirstVisit,start with '7'  
        graph.resetVertices();  
        graph.breadthFirstVisit('0');//breadthFirstVisit,start with '0'  
          
        //shortest path  
        Stack<Vertex> pathStack=new Stack<Vertex>();  
        //from '0' to '7'.  
        double pathLength=graph.getShortestPath('0','7',pathStack);  
        System.out.println(pathLength);  
        while(!pathStack.isEmpty()){  
            Vertex vertex=pathStack.pop();  
            System.out.print(vertex.getLabel()+" ");  
        }  
          
        //BasicGraphInterface<String> airMap=new UndirectedGraph<String>();  
        //airMap.  
          
    }  
      
    public static Graph createGapth(){  
        /* 
         0----1---2 
         | \  |   | 
         |  \ |   | 
         |   \|   | 
         3    4   5 
         |   / 
         |  / 
         | / 
         6---7 
         the adjacent List is : 
         0-->4--3--1 
         1-->4--2--0 
         2-->5--1 
         3-->6--0 
         4-->6--1--0 
         5-->2 
         6-->7--4--3 
         7-->6 
         */  
          
        Graph graph=new Graph();  
        graph.addVertex('0');  
        graph.addVertex('1');  
        graph.addVertex('2');  
        graph.addVertex('3');  
        graph.addVertex('4');  
        graph.addVertex('5');  
        graph.addVertex('6');  
        graph.addVertex('7');  
          
        graph.addEdge('0','4');  
        graph.addEdge('0','3');  
        graph.addEdge('0','1');  
          
        graph.addEdge('1','4');  
        graph.addEdge('1','2');  
        graph.addEdge('1','0');  
          
        graph.addEdge('2','5');  
        graph.addEdge('2','1');  
          
        graph.addEdge('3','6');  
        graph.addEdge('3','0');  
          
        graph.addEdge('4','6');  
        graph.addEdge('4','1');  
        graph.addEdge('4','0');  
          
        graph.addEdge('5','2');  
          
        graph.addEdge('6','7');  
        graph.addEdge('6','4');  
        graph.addEdge('6','3');  
          
        graph.addEdge('7','6');  
          
        return graph;  
    }  
}  
  
  
class Vertex{  
    private char label;  
    private List<Edge> edgeList;  
    private boolean isVisited;  
    private Vertex previousVertex;//use it in the method-'getShortestPath()'  
    private double cost;//the cost from beginning to this vertex   
      
    public Vertex(char label){  
        this.label=label;  
        edgeList=new ArrayList<Edge>();  
        isVisited=false;  
        previousVertex=null;  
        cost=0;  
    }  
    public boolean isVisited(){  
        return isVisited;  
    }  
    public void visit(){  
        System.out.println(this.label);  
        this.isVisited=true;  
    }  
      
    public void setPreviousVertex(Vertex vertex){  
        this.previousVertex=vertex;  
    }  
    public void setVisited(Boolean isVisited){  
        this.isVisited=isVisited;  
    }  
    public void setCost(double cost){  
        this.cost=cost;  
    }  
    public Vertex getFirstNeighbor(){  
        Vertex neighbor=this.edgeList.get(0).endVertex;  
        return neighbor;  
    }  
    public char getLabel(){  
        return this.label;  
    }  
      
    public double getCost(){  
        return this.cost;  
    }  
    public Vertex getPreviousVertex(){  
        return this.previousVertex;  
    }  
    public Vertex getFirstUnvisitedNeighbor(){  
        Vertex unVisitedNeighbor=null;  
        for(int i=0,len=edgeList.size();i<len;i++){  
            Vertex vertex=edgeList.get(i).endVertex;  
            if(!vertex.isVisited){  
                unVisitedNeighbor=vertex;  
                break;  
            }  
        }  
        return unVisitedNeighbor;  
    }  
    public boolean equals(Object object){  
        boolean result=false;  
        if(this==object)return true;  
        if(object instanceof Vertex){  
            Vertex otherVertex=(Vertex)object;  
            result=this.label==otherVertex.label;  
        }  
        return result;  
    }  
    public boolean connect(Vertex endVertex,double weight){  
          
        boolean result=false;//result=true if successful  
          
        if(!this.equals(endVertex)){//connections should occur in different Vertices  
            boolean isDuplicateEdge=false;  
            List<Edge> edgeList=this.edgeList;  
            if(edgeList.contains(endVertex)){  
                isDuplicateEdge=true;  
            }  
            if(!isDuplicateEdge){  
                //endVertex.previousVertex=this;  
                edgeList.add(new Edge(endVertex,weight));  
                result=true;  
            }  
        }  
              
        return result;  
    }  
      
    public boolean hasNeighbor(){  
        return !edgeList.isEmpty();  
    }  
    protected  class Edge{  
        //A-->B,then the "outerClass" which invokes the method "getEndVertex"   
        //is "A",the "endVertex" is "B"  
        private Vertex endVertex;  
        private double weight;  
          
        protected Edge(Vertex endVertex,double weight){  
            this.endVertex=endVertex;  
            this.weight=weight;  
        }  
        protected Vertex getEndVertex(){  
            return endVertex;  
        }  
        protected double getWeight(){  
            return weight;  
        }  
          
    }  
}  