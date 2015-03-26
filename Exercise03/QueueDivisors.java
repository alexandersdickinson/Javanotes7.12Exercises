/**
A rehash of an exercise from chapter 3 that finds the number with the largest number of divisors in the range of 1 to 100,000. This version simply uses threads.
*/

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueDivisors{
	
	private static final int SAMPLE_SIZE = 100000;
	private static final int THREAD_COUNT = 4;
	private static final int TASK_COUNT = 25;
	private static int maxDividend;
	private static int maxDivisorCount = 0;
	private static ConcurrentLinkedQueue<Runnable> tasks;
	private static volatile boolean running = true;
	private static volatile int taskNum = 1;
	
	public static void main(String[] args){
		
		int start = 1;
		int taskInterval = SAMPLE_SIZE / TASK_COUNT;
		int end = taskInterval;
		DivisorThread[] threads = new DivisorThread[THREAD_COUNT];
		tasks = new ConcurrentLinkedQueue<Runnable>();
		
		for(int i = 0; i < TASK_COUNT; i ++){
			DivisorTask task = new DivisorTask(start, end);
			tasks.add(task);
			start += taskInterval;
			end += taskInterval;
		}
		
		for(int i = 0; i < THREAD_COUNT; i++){
			threads[i] = new DivisorThread();
		}
		
		for(int i = 0; i < THREAD_COUNT; i++){
			threads[i].start();
		}
		
		for(int i = 0; i < THREAD_COUNT; i++){
			while(threads[i].isAlive()){
				try{
					threads[i].join();
				}
				catch(InterruptedException e){
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("The number with the most divisors is " + maxDividend);
		System.out.println(maxDivisorCount);
		
	}
	
	static class DivisorTask implements Runnable{
		
		static int start;
		static int end;
		
		public DivisorTask(int start, int end){
			this.start = start;
			this.end = end;
		}
		
		public void run(){
			
			System.out.println("Thread running...");
			divisorCount(start, end);
			if(taskNum >= TASK_COUNT)
				running = false;
			taskNum++;
			System.out.println(taskNum);
			
		}
		
	}
	
	static class DivisorThread extends Thread{
		
		public void run(){
			
			Runnable task = tasks.poll();
			while(running){
				if(task == null)
					break;
				task.run();
			}
			
		}
		
	}
		
	private static void divisorCount(int start, int end){
		
		int divisorCount;//number of divisors for number being divided
		int maxThreadDividend = start;
		int maxThreadDivisorCount = 0;
	
		for (int dividend = start; dividend <= end; dividend++){//iterate through dividends
		
			divisorCount = 1;
		
			for (int divisor = 1; divisor <= dividend/2; divisor++){//iterate through divisors
			
				if (dividend % divisor == 0){
					divisorCount++;
				}//end if
				
			}//end for
		
			if (divisorCount > maxThreadDivisorCount){
				maxThreadDivisorCount = divisorCount;
				maxThreadDividend = dividend;
			}
		
		}//end for
		
		report(maxThreadDivisorCount, maxThreadDividend);
		
	}
	
	private synchronized static void report(int maxThreadDivisorCount, int maxThreadDividend){
		
		if(maxThreadDivisorCount > maxDivisorCount){
			maxDivisorCount = maxThreadDivisorCount;
			maxDividend = maxThreadDividend;
		}
		
	}
	
}