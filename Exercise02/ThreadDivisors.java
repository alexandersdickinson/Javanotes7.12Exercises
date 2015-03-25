/**
A rehash of an exercise from chapter 3 that finds the number with the largest number of divisors in the range of 1 to 100,000. This version simply uses threads.
*/

public class ThreadDivisors{
	
	private static final int SAMPLE_SIZE = 100000;
	private static final int THREAD_COUNT = 4;
	private static int maxDividend;
	private static int maxDivisorCount = 0;
	
	public static void main(String[] args){
		
		int start = 1;
		int end = SAMPLE_SIZE / THREAD_COUNT;
		DivisorThread[] threads = new DivisorThread[THREAD_COUNT];
		
		for(int i = 0; i < THREAD_COUNT; i++){
			threads[i] = new DivisorThread(start, end);
			start += SAMPLE_SIZE / THREAD_COUNT;
			end += SAMPLE_SIZE / THREAD_COUNT;
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
	
	static class DivisorThread extends Thread{
		
		static int start;
		static int end;
		
		public DivisorThread(int start, int end){
			this.start = start;
			this.end = end;
		}
		
		public void run(){
			
			System.out.println("Thread running...");
			divisorCount(start, end);
			
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