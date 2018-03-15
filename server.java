import java.net.*;
import java.io.*;

public class server {
	
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
				System.out.println("You make it here");
				clientSocket = serverSocket.accept();
				System.out.println("And here");
			}
			
			catch (IOException e) {
				System.out.println("Exception thrown while trying to connect on port " 
					+ portNumber);
			}
			
			ServerThread thisone = new ServerThread(clientSocket);
			thisone.start();
		}
	}
}