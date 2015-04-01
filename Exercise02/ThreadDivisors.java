/**
A rehash of an exercise from chapter 3 that finds the number with the largest number of divisors in the range of 1 to 100,000. This version simply uses threads.
*/

public class ThreadDivisors{
	
	private static final int DIVIDEND_COUNT = 100000;
	private static DivisorThread[] threads;
	private static int dividend;//dividend with the most divisors.
	private static int divisorCount;//number of divisors for the dividend with the most divisors
	
	public static void main(String[] args){
		
		threads = new DivisorThread[Runtime.getRuntime().availableProcessors()];
		int start = 1;
		int taskSize;
		taskSize = DIVIDEND_COUNT / threads.length;
		int end = taskSize;
			
		for(int i = 0; i < threads.length; i++){//create threads
			
			threads[i] = new DivisorThread(start, end);
			start += taskSize;
			end += taskSize;
			
		}
		
		for(int i = 0; i < threads.length; i++){
			threads[i].start();
		}
		
		for(int i = 0; i < threads.length; i++){
			try{
				threads[i].join();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
		System.out.println("The dividend with the most divisors is " + dividend + " and has " + divisorCount + " divisors.");
		
	}
	
	private static class DivisorThread extends Thread{
		
		int start;
		int end;
		
		public DivisorThread(int start, int end){
			this.start = start;
			this.end = end;
		}
		
		/**
			This thread finds the first number with the most dividends in the range given.
		*/
		public void run(){
			
			int maxDividend = 0;//number with the most divisors in the given range.
			int maxDivisors = 0;//number of divisors for the dividend with the largest number of divisors in the given range.
			int divisorCount;//number of divisors for the current dividend.
			System.out.println("Thread working...");
			
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
			
			updateGreatest(maxDividend, maxDivisors);
			
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