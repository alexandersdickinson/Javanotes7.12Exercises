/**
A rehash of an exercise from chapter 3 that finds the number with the largest number of divisors in the range of 1 to 100,000. This is an augmented version of
exercise02, using a task queue and a results queue.
*/

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueDivisors{
	
	private static final int DIVIDEND_COUNT = 100000;
	private static final int TASK_COUNT = 100;
	private static DivisorThread[] threads;
	private static int dividend;//dividend with the most divisors.
	private static int divisorCount;//number of divisors for the dividend with the most divisors
	private static ConcurrentLinkedQueue<Runnable> taskQueue;
	private static LinkedBlockingQueue<Runnable> resultQueue;
	private static volatile int taskNum = 1;
	
	public static void main(String[] args){
		
		threads = new DivisorThread[(Runtime.getRuntime().availableProcessors()) * 4];
		int start = 1;
		int taskSize = DIVIDEND_COUNT / TASK_COUNT;
		int end = taskSize;
		taskQueue = new ConcurrentLinkedQueue<Runnable>();
		resultQueue = new LinkedBlockingQueue<Runnable>();
		
		for(int i = 0; i < TASK_COUNT; i ++){//add tasks to task queue
			taskQueue.add(new DivisorTask(start, end));
			start += taskSize;
			end += taskSize;
		}
			
		for(int i = 0; i < threads.length; i++){//create threads
			threads[i] = new DivisorThread();
		}
		
		for(int i = 0; i < threads.length; i++){
			threads[i].start();
		}
		
		for(int i = 0; i < TASK_COUNT; i++){
			try{
				Runnable task = resultQueue.take();
				task.run();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
		System.out.println("The dividend with the most divisors is " + dividend + " and has " + divisorCount + " divisors.");
		
	}
	
	private static class DivisorThread extends Thread{
		
		/**
			This thread finds the first number with the most dividends in the range given.
		*/
		public void run(){
			
			while(true){//poll tasks until there are none.
				Runnable task = taskQueue.poll();
				if(task == null){
					break;
				}
				else{
					task.run();
				}
			}
			
		}
		
	}
	
	private static class DivisorTask implements Runnable{
		
		int start;
		int end;
		
		public DivisorTask(int start, int end){
			this.start = start;
			this.end = end;
		}
		
		/**
			This thread finds the first number with the most dividends in the range given.
		*/
		public void run(){
			System.out.println("Thread working on task #" + taskNum + "...");
			countDivisors(start, end);
			taskNum++;
		}
		
	}
	
	private static class DivisorResult implements Runnable{
		
		int maxDividend;
		int maxDivisors;
		
		public DivisorResult(int maxDividend, int maxDivisors){
			this.maxDividend = maxDividend;
			this.maxDivisors = maxDivisors;
		}
		
		public void run(){
			System.out.println("Updating result...");
			updateGreatest(maxDividend, maxDivisors);
		}
		
	}
	
	/**
		This subroutine finds the dividend with the greatest number of divisors in the given range.
		
		@param start Start of the given range.
		@param end End of the given range.
	*/
	private static void countDivisors(int start, int end){
		
		int maxDividend = 0;//number with the most divisors in the given range.
		int maxDivisors = 0;//number of divisors for the dividend with the largest number of divisors in the given range.
		int divisorCount;//number of divisors for the current dividend.
		
		//iterate through the dividends in the range given
		for(int i = start; i <= end; i++){
			
			divisorCount = 1;
			
			for(int j = 1; j <= i/2; j++){//iterate through divisors
				
				if(i % j == 0){
					divisorCount++;
				}
				
			}
			
			if(divisorCount > maxDivisors){
				maxDividend = i;
				maxDivisors = divisorCount;
			}
			
		}
		
		try{
			resultQueue.put(new DivisorResult(maxDividend, maxDivisors));
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		
	}
	
	/**
		This subroutine takes the number with the greatest number of divisors from each thread and compares it to the number
		with the greatest number of divisors found by other threads. If the new dividend has more divisors than the old
		dividend, the relevant values are updated.
		
		@param rangeDividend The dividend found by the calling thread that has the most divisors of all dividends in the thread's
							 given range.
		@param rangeDivisors The number of divisors of the dividend with the most divisor's in the calling thread's given range.
	*/
	private static synchronized void updateGreatest(int rangeDividend, int rangeDivisors){
		
		if(rangeDivisors > divisorCount){
			divisorCount = rangeDivisors;
			dividend = rangeDividend;
		}
		
	}
	
}