import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.*;

public class ServerThread extends Thread {
	protected Socket socket;
	protected String user;
	protected BlockingQueue<String> queue = new ArrayBlockingQueue(10);
	
	public ServerThread(Socket clientSocket) {
		this.socket = clientSocket;
	}
	
	public void run() {
		System.out.println("Connection with " + socket.getInetAddress() + " begun.");
		queue.offer("What is your username?");
		Responder responder = new Responder();
		Thread t = new Thread(responder);
		t.start();
		try {
			queue.offer("What is your username?");
			BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
			String inputLine;
			this.user = in.readLine();
			queue.offer(user + " has logged on");
			while ((inputLine = in.readLine()) != null) {
				queue.offer(user + " says: " + inputLine);
			}
		}
		
		catch (IOException e) {
			System.err.println("Connection Lost");
			return;
		}
	}
	
	class Responder implements Runnable {
		
		public void run(){
			String output = null;
			try {
				PrintWriter out =
					new PrintWriter(socket.getOutputStream(), true);
				while (true) {
					if ((output = queue.poll()) != null)
						out.println(output);
				}
			}
			
			catch (IOException e) {
				System.err.println("Connection Lost");
				return;
			}
		}
	}
}