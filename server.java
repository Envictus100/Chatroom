import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.*;

public class server {
	private ArrayList<ServerThread> threadList = new ArrayList<>();
	
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
	public void runit()
	{
		int portNumber = 999;
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			serverSocket =
                new ServerSocket(portNumber);
		}
		
		catch (IOException e) {
			System.out.println("Exception caught while trying to listen on port "
				+ portNumber);
			System.out.println(e.getMessage());
		}
		
		while (true) {
			try {
				clientSocket = serverSocket.accept();
			}
			
			catch (IOException e) {
				System.out.println("Exception thrown while trying to connect on port " 
					+ portNumber);
			}
			
			ServerThread thisone = new ServerThread(clientSocket, threadList);
			Thread t = new Thread(thisone);
			t.start();
			synchronized (threadList) {
				threadList.add(thisone);
			}
		}
	}
}