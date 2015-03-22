/**
This program starts as many threads as the user desires, which increment as many times as the user wants. This is an exercise in unsynchronized threads, so this program is actually designed to fail. With enough parallel threads and with a large enough number, it should be easy to get counts from the user and the threads that do not match up.
*/

import java.util.Scanner;

public class UnsyncedThreads{
	
	static Counter counter;
	static int incrCount;//number of incrementations each thread performs.
	static IncrementThread[] threads;
	
	public static void main(String[] args){
		
		Scanner input = new Scanner(System.in);//for user input
		int threadCount;//number of threads.
		boolean quit;
		
		System.out.println("This program increments to a certain number");
		System.out.println("based on the number of threads that perform");
		System.out.println("a certain number of increment operations.");
		
		do{
		
			while(true){
			
				System.out.println("\nPlease type in the number of threads you'd like to use.");
				System.out.println("This can be any number from 1 to 500.");
			
				threadCount = input.nextInt();
				if(threadCount > 500 || threadCount < 1){
					System.out.println("\nInput is not in the valid range. Please try again.");
				}
				else
					break;
			
			}
		
			while(true){
			
				System.out.println("\nPlease type in the number of increment to be performed");
				System.out.println("by each thread.");
				System.out.println("This can be any number from 1 to 1,000,000.");
			
				incrCount = input.nextInt();
				if(incrCount > 1000000 || incrCount < 1){
					System.out.println("Input is not in the valid range. Please try again.");
				}
				else
					break;
			
			}
		
			threads = new IncrementThread[threadCount];
			counter = new Counter();
			System.out.println("\nIncrementing...");
			for(int i = 0; i < threadCount; i++){//initialize thread array
				threads[i] = new IncrementThread();
			}
		
			for(int i = 0; i < threadCount; i++){
				threads[i].start();
			}
		
			for(int i = 0; i < threadCount; i++){
				try{
					threads[i].join();
				}
				catch(InterruptedException e){
					System.out.println(e.getMessage());
				}
			}
		
			System.out.println("Expected number is: " + (threadCount * incrCount));
			System.out.println("Number returned by threads is: " + counter.getCount());
			System.out.println("Would you like to quit?");
			quit = input.nextBoolean();
		
		}while(!quit);
		
	}
	
	static class IncrementThread extends Thread{
		
		public void run(){
			
			for(int i = 0; i < incrCount; i++){
				counter.inc();
			}
		}
		
	}
	
	static class Counter {
	    int count;
	    void inc() {
	        count = count+1;
	    }
	    int getCount() {
	        return count;
	    }
	}
	
	
}