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
						messageParser(input);
					}
				}
			}
			
			catch (IOException e) {
				System.err.println("Connection Lost");
				return;
			}
		}
		
		public void messageParser(String message) {
			String[] messageArray = message.split(" ");
			message = "";

			//Handles case if File is received
			if(messageArray[0].equalsIgnoreCase("/file")) {
				
				for(int i = 2; i < messageArray.length; i++)
					message = message + messageArray[i] + " ";
				
				String home = System.getProperty("user.home");
				String[] filePath = messageArray[1].split("/");
				String fileName = filePath[filePath.length-1];
				
				try{
					File file = new File(home+"/Downloads/" + fileName);
					System.out.println("*Downloading " + fileName + " to " + file + " *");
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					writer.write(message);
					writer.close();
					System.out.println("*" + fileName + " Downloaded*");
				}
				
				catch(Exception e) {
					System.out.println("Problem saving a file: " + messageArray[1]);
				}
			}
			
			//Handles case if message is received
			else if(messageArray[0].equalsIgnoreCase("/msg")) {
				for(int i = 1; i < messageArray.length; i++)
				message = message + messageArray[i] + " ";
				System.out.println(message);
			}
			
			//Handles all other cases
			else {
				System.out.println("You broke it dood: " + message);
			}
		}
	}
}