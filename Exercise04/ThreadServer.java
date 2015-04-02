/**
Given the name of a directory, this program can be used to print the directory's contents. If the client sends the command, "INDEX," the list of files in
the chosen directory will be sent to the client's outputstream. If the command is GET <fileName>, the program checks to see if the file exists, and then
sends the contents of that file.
*/

import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

public class ThreadServer{
	
	private static final int LISTENING_PORT = 10000;
	private static final int THREAD_POOL_SIZE = 5;
	private static final int QUEUE_SIZE = 8;
	private static ArrayBlockingQueue<Socket> connectionQueue;
	private static File directory;
		
	public static void main(String[] args){
		
		Scanner scanner = new Scanner(System.in);
		
		//Create directory object.
		if(args.length == 1){
			directory = new File(args[0]);
		}
		else{
			System.out.println("No name for the directory was provided as a command line argument.");
			System.out.println("Please type in the name of the desired directory.");
			directory = new File(scanner.nextLine());
			scanner.close();
		}
		
		if(directory.isDirectory()){
			
			if(!directory.exists()){
				System.out.println("This directory does not exist.");
				System.out.println("Goodbye!");
				return;
			}
		
		}
		else{
			System.out.println("The chosen file is not a directory.");
			System.out.println("Goodbye!");
			return;
		}
		
		connectionQueue = new ArrayBlockingQueue<Socket>(8);
		
		for(int i = 0; i < THREAD_POOL_SIZE; i++){//create threads
			new ServerThread();
		}
	
		try{
		
			ServerSocket server = new ServerSocket(LISTENING_PORT);
		
			while(true){
				System.out.println("Listening on port " + LISTENING_PORT);
				Socket connection = server.accept();
				try{
					connectionQueue.put(connection);
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		
		}
		catch(IOException e){
			System.out.println("Error has occurred in connecting with the client: " + e);
		}
		
	}
	
	private static class ServerThread extends Thread{
		
		public ServerThread(){
			setDaemon(true);
			start();
		}
		
		public void run(){
			while(true){
				try{
					Socket connection;
					connection = connectionQueue.take();
					directoryInfo(connection);
				}
				catch(InterruptedException e){
					continue;
				}
			}
		}
		
	}
	
	/**
		This subroutine waits for a command from the client and provides the appropriate information about the directory specified in server's
		command-line argument.
		Precondition: There must be a connection to a client. A file that is a directory and that exists must be given as well.
		Postcondition: Either a list of files in the chosen directory will be sent to the client, or the name of the directory itself,
		depending on the command given by the client.
		@param connection The client socket returned by server.accept() in main().
	*/
	static void directoryInfo(Socket connection){
		
		String[] files = directory.list();//List of the files in the chosen directory.
		File txtFile;
		String fileName;
		String inputToken;
				
		try{
			
			System.out.println("Now connected to " + connection.getInetAddress().toString());
			PrintWriter output = new PrintWriter(connection.getOutputStream());
			output.println("You have connected to the host");
			output.println("Please type in a command.");
			output.println("The chosen directory is " + directory.getName());
			output.println();
			output.flush();
			Scanner input = new Scanner(new InputStreamReader(connection.getInputStream()));
			inputToken = input.next();
			
			if(inputToken.equalsIgnoreCase("INDEX")){
				//send list of files in directory through connection's outputstream.				
				for(String file:files){
					output.println(file);
					output.flush();
				}
			}
			else if(inputToken.equalsIgnoreCase("GET")){
				//send confirmation that file exists, and if so, the contents of that file.
				input.useDelimiter("[\\<\\>]");
				input.next();
				fileName = input.next();
				
				txtFile = new File(directory, fileName);
				if(!txtFile.exists()){
					output.println("This file does not exist in the chosen directory.");
					output.flush();
				}
								
				input.reset();//use whitespace delimiter again
					
				try{
					copyFile(txtFile, connection);
				}
				catch(IOException e){
					output.println("An error occurred while processing file: " + e);
					e.printStackTrace();
				}
				
			}
			else{//Command is not of the correct format.
				output.println("Command is invalid.");
				output.flush();
			}
			
			output.close();
			connection.close();
			
		}
		catch(Exception e){
			System.out.println("Error: " + e);
		}
		
	}
	
	/**
		This subroutine takes a file and copies it to a file called "copy" in the documents folder.
		Precondition: A source file of extension .txt that is in the directory being accessed by the server.
		Postcondition: A copy of the source file is created in the documents folder of the client.
		@param source A source .txt file.
		@throws Exception An error in creating the copy.
	*/
	static void copyFile(File source, Socket connection) throws Exception{
		
		String line;
		Scanner fileRead = new Scanner(new FileReader(source));//Reads from source
		PrintWriter filePush = new PrintWriter(connection.getOutputStream());
		
		while(fileRead.hasNext()){
			line = fileRead.nextLine();
			filePush.println(line);
		}
		
		filePush.flush();
		fileRead.close();
		filePush.close();
		
	}
	
}