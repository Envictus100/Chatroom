import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.*;

public class server {
	private ArrayList<ServerThread> threadList = new ArrayList<ServerThread>();
	private String password;
	
	public static void main(String args[]) throws IOException {
		if (args.length != 0) {
			System.err.println("Please use 'java server'");
			System.exit(1);
		}
		
		try {
			new server().runit();
		}
		
		catch(Throwable t) {
			t.printStackTrace(System.err);
		}
	}
	//Gets run by an instance of the server class. Contains all the important stuff.
	public void runit()
	{
		System.out.println("Please set a password. Make it a good one!");
		this.password = new Scanner(System.in).nextLine();
		
		int portNumber = 999;
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		
		//Attempts to create a new server socket
		try {
			serverSocket =
                new ServerSocket(portNumber);
		}
		
		catch (IOException e) {
			System.out.println("Exception caught while trying to listen on port "
				+ portNumber);
			System.out.println(e.getMessage());
		}
		
		//Attempts to establish the client-server relationship
		while (true) {
			try {
				clientSocket = serverSocket.accept();
			}
			
			catch (IOException e) {
				System.out.println("Exception thrown while trying to connect on port " 
					+ portNumber);
			}
			
			//Creates a new instance of ServerThread for each connection, which is where
			//all the cool magic happens
			ServerThread thisone = new ServerThread(clientSocket, threadList, password);
			Thread t = new Thread(thisone);
			t.start();
			
			//Make sure the array list is synced, so we don't get any competition
			//errors from our threads
			synchronized (threadList) {
				threadList.add(thisone);
			}
		}
	}
}