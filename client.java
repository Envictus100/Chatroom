import java.io.*;
import java.net.*;

public class client {
	public Socket echoSocket;
	
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
	public void runit(String hostName)
	{
		int portNumber = 999;

		try {
			this.echoSocket = new Socket(hostName, portNumber);
			Printer printer = new Printer();
			Thread t = new Thread(printer);
			t.start();
			PrintWriter out =
				new PrintWriter(echoSocket.getOutputStream(), true);
			BufferedReader stdIn =
				new BufferedReader(
				new InputStreamReader(System.in));
			String userInput;
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
	
	class Printer implements Runnable {
		
		public void run(){
			String input = null;
			try {
				BufferedReader in =
						new BufferedReader(
						new InputStreamReader(echoSocket.getInputStream()));
				while (true) {
					if ((input = in.readLine()) != null)
						System.out.println(input);
				}
			}
			
			catch (IOException e) {
				System.err.println("Connection Lost");
				return;
			}
		}
	}
}