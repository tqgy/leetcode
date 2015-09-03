package com.carrerup.concurrent;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumer {
	
	//Traditional using wait() and notify()
	class Sycn1 {
		private LinkedList<Object> myList = new LinkedList<Object>();
		private int MAX = 10;

		public Sycn1() {
		}

		public void start() {
			new Producer().start();
			new Consumer().start();
		}

		class Producer extends Thread {
			public void run() {
				while (true) {
					synchronized (myList) {
						try {
							while (myList.size() == MAX) {
								System.out.println("warning: it's full!");
								myList.wait();
							}
							Object o = new Object();
							if (myList.add(o)) {
								System.out.println("Producer: " + o);
								myList.notify();
							}
						} catch (InterruptedException ie) {
							System.out.println("producer is interrupted!");
						}
					}
				}
			}
		}

		class Consumer extends Thread {
			public void run() {
				while (true) {
					synchronized (myList) {
						try {
							while (myList.size() == 0) {
								System.out.println("warning: it's empty!");
								myList.wait();
							}
							Object o = myList.removeLast();
							System.out.println("Consumer: " + o);
							myList.notify();
						} catch (InterruptedException ie) {
							System.out.println("consumer is interrupted!");
						}
					}
				}
			}
		}

	}
	
	 class Sycn2{  
		    private LinkedList<Object> myList = new LinkedList<Object>();  
		    private int MAX = 10;  
		    private final Lock lock = new ReentrantLock();  
		    private final Condition full = lock.newCondition();  
		    private final Condition empty = lock.newCondition();  
		      
		    public Sycn2(){  
		    }  
		      
		    public void start(){  
		            new Producer().start();  
		            new Consumer().start();  
		    }   
		      
		    class Producer extends Thread{          
		        public void run(){  
		            while(true){  
		                lock.lock();  
		                try{  
		                    while(myList.size() == MAX){  
		                        System.out.println("warning: it's full!");  
		                        full.await();  
		                    }  
		                    Object o = new Object();  
		                    if(myList.add(o)){  
		                        System.out.println("Producer: " + o);  
		                        empty.signal();  
		                    }  
		                }catch(InterruptedException ie){  
		                    System.out.println("producer is interrupted!");  
		                }finally{  
		                    lock.unlock();  
		                }  
		            }  
		        }  
		    }  
		      
		    class Consumer extends Thread{  
		        public void run(){  
		            while(true){  
		                lock.lock();  
		                try{  
		                    while(myList.size() == 0){  
		                        System.out.println("warning: it's empty!");  
		                        empty.await();  
		                    }  
		                    Object o = myList.removeLast();  
		                    System.out.println("Consumer: " + o);  
		                    full.signal();  
		                }catch(InterruptedException ie){  
		                    System.out.println("consumer is interrupted!");  
		                }finally{  
		                    lock.unlock();  
		                }  
		            }  
		        }  
		    }  
		      
		}  
	 
	 class Sycn3{  
		    private LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<Object>(10);  
		    private int MAX = 10;  
		      
		    public Sycn3(){  
		    }  
		      
		    public void start(){  
		            new Producer().start();  
		            new Consumer().start();  
		    }  

		    class Producer extends Thread{          
		        public void run(){  
		            while(true){  
		                //synchronized(this){  
		                try{  
		                    if(queue.size() == MAX)  
		                        System.out.println("warning: it's full!");  
		                    Object o = new Object();  
		                    queue.put(o);  
		                    System.out.println("Producer: " + o);  
		               }catch(InterruptedException e){  
		                    System.out.println("producer is interrupted!");  
		               }  
		                //}  
		            }  
		        }  
		    }  
		      
		    class Consumer extends Thread{  
		        public void run(){  
		            while(true){  
		                //synchronized(this){  
		                try{  
		                    if(queue.size() == 0)  
		                        System.out.println("warning: it's empty!");  
		                    Object o = queue.take();  
		                    System.out.println("Consumer: " + o);  
		                }catch(InterruptedException e){  
		                    System.out.println("producer is interrupted!");  
		                }  
		                //}  
		            }  
		        }  
		    }  
		      
		}  
	 
	 class Sycn4{  
		    private PipedOutputStream pos;  
		    private PipedInputStream pis;  
		    //private ObjectOutputStream oos;  
		    //private ObjectInputStream ois;  
		      
		    public Sycn4(){  
		        try{  
		            pos = new PipedOutputStream();  
		            pis = new PipedInputStream(pos);  
		            //oos = new ObjectOutputStream(pos);  
		            //ois = new ObjectInputStream(pis);  
		        }catch(IOException e){  
		            System.out.println(e);  
		        }  
		    }  
		      
		    public void start(){  
		        new Producer().start();  
		        new Consumer().start();  
		    }  
   
		    class Producer extends Thread{  
		        public void run() {  
		            try{  
		                while(true){  
		                    int b = (int) (Math.random() * 255);  
		                    System.out.println("Producer: a byte, the value is " + b);  
		                    pos.write(b);  
		                    pos.flush();  
		                    //Object o = new MyObject();  
		                    //oos.writeObject(o);  
		                    //oos.flush();  
		                    //System.out.println("Producer: " + o);  
		                }  
		            }catch(Exception e){  
		                //System.out.println(e);  
		                e.printStackTrace();  
		            }finally{  
		                try{  
		                    pos.close();  
		                    pis.close();  
		                    //oos.close();  
		                    //ois.close();  
		                }catch(IOException e){  
		                    System.out.println(e);  
		                }  
		            }  
		        }  
		    }  
		      
		    class Consumer extends Thread{  
		        public void run(){  
		            try{  
		                while(true){  
		                    int b = pis.read();  
		                    System.out.println("Consumer: a byte, the value is " + String.valueOf(b));  
		                    //Object o = ois.readObject();  
		                    //if(o != null)  
		                        //System.out.println("Consumer: " + o);  
		                }  
		            }catch(Exception e){  
		                //System.out.println(e);  
		                e.printStackTrace();  
		            }finally{  
		                try{  
		                    pos.close();  
		                    pis.close();  
		                    //oos.close();  
		                    //ois.close();  
		                }catch(IOException e){  
		                    System.out.println(e);  
		                }  
		            }  
		        }  
		    }  
		      
		    //class MyObject implements Serializable {  
		    //}  
		}  

	public static void main(String[] args) throws Exception {
		ProducerConsumer pc = new ProducerConsumer();
//		Sycn1 s1 = pc.new Sycn1();
//		s1.start();
		
//		Sycn2 s2 = pc.new Sycn2();
//		s2.start();
//		
//		Sycn3 s3 = pc.new Sycn3();
//		s3.start();
//		
		Sycn4 s4 = pc.new Sycn4();
		s4.start();
	}
}
