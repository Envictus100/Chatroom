import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.*;

public class ServerThread implements Runnable {
	protected Socket socket;
	protected String user;
	private ArrayList<ServerThread> otherThreads;
	protected BlockingQueue<String> queue = new ArrayBlockingQueue(10);
	
	public ServerThread(Socket clientSocket, ArrayList<ServerThread> runnables) {
		this.socket = clientSocket;
		this.otherThreads = runnables;
	}
	
	public String getName() {
		return this.user;
	}
	
	public void addToQueue(String msg) {
		queue.offer(msg);
	}
	
	public void sendToThread(String msg, String threadName) {
		msg = user + " says: " + msg;
		queue.offer(msg);
	}
	
	public void run() {
		System.out.println("Connection with " + socket.getInetAddress() + " begun.");
		Responder responder = new Responder();
		Thread t = new Thread(responder);
		t.start();
		try {
			queue.offer("What is your username?");
			BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
			String inputLine;
			this.user = in.readLine();
			Thread.currentThread().setName(user);
			System.out.println(Thread.currentThread().getName());
			queue.offer(user + " has logged on");
			while ((inputLine = in.readLine()) != null) {
				//queue.offer(user + " says: " + inputLine);
				//sendToThread(inputLine, " ");
				for(int i = 0; i < otherThreads.size(); i++)
					synchronized (otherThreads) {
						if(this.user == otherThreads.get(i).getName())
							addToQueue("You said: " + inputLine);
						else
							otherThreads.get(i).addToQueue("~~~" + user + " says: " + inputLine);
					}
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