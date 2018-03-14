import java.io.*;
import java.net.*;

public class client {

	public static void main(String args[]) throws IOException {
		
		if (args.length != 2) {
			System.err.println("Please use 'java client <hostname> <port>'");
			System.exit(1);
		}
		
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);

		try {
			Socket echoSocket = new Socket(hostName, portNumber);
			PrintWriter out =
				new PrintWriter(echoSocket.getOutputStream(), true);
			BufferedReader in =
				new BufferedReader(
				new InputStreamReader(echoSocket.getInputStream()));
			BufferedReader stdIn =
				new BufferedReader(
				new InputStreamReader(System.in));
			String userInput;
			
			while ((userInput = stdIn.readLine()) != null) {
				out.println(userInput);
				System.out.println("echo: " + in.readLine());
			}
		}
		
		catch (UnknownHostException e) {
			System.err.println("I'm not familiar with host " + hostName);
			System.exit(1);
		}
		
		catch (IOException e) {
			System.err.println("I can't seem to get and I/O connection to " + hostName);
			System.exit(1);
		}
	}
}