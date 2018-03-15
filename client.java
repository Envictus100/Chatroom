import java.io.*;
import java.net.*;

public class client {
	public Socket clientSocket;
	
	public static void main(String args[]) throws IOException {
		
		if (args.length != 1) {
			System.err.println("Please use 'java client <hostname>'");
			System.exit(1);
		}
		
		try {
			new client().runit(args[0]);
		}
		
		catch(Throwable t) {
			t.printStackTrace(System.err);
		}
	}	
	
	//Runs as an instance of the client class
	//This is where the cool stuff is
	public void runit(String hostName)
	{
		int portNumber = 999;

		try {
			//Creates a new socket connection using the preassigned port
			this.clientSocket = new Socket(hostName, portNumber);
			
			//Creates a new Printer thread to monitor for incoming messages
			//from the server
			Printer printer = new Printer();
			Thread t = new Thread(printer);
			t.start();
			
			
			PrintWriter out =
				new PrintWriter(clientSocket.getOutputStream(), true);
				
			BufferedReader stdIn =
				new BufferedReader(
				new InputStreamReader(System.in));
			String userInput;
			
			//And here is where we monitor for input from the user
			while ((userInput = stdIn.readLine()) != null) {
				out.println(userInput);
			}
		}
		
		catch (UnknownHostException e) {
			System.err.println("I'm not familiar with host " + hostName);
			System.exit(1);
		}
		
		catch (IOException e) {
			System.err.println("I can't seem to get an I/O connection to " + hostName);
			System.exit(1);
		}
	}
	
	//A separate thread that monitors for messages from the server and prints them to the screen.
	class Printer implements Runnable {
		
		public void run(){
			String input = null;
			try {
				BufferedReader in =
						new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
				
				//This is where it monitors for incoming messages
				while (true) {
					if ((input = in.readLine()) != null) {
						System.out.println(input);
					}
				}
			}
			
			catch (IOException e) {
				System.err.println("Connection Lost");
				return;
			}
		}
	}
}