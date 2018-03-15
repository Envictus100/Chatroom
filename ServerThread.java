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
	
	//Constructor
	public ServerThread(Socket clientSocket, ArrayList<ServerThread> runnables) {
		this.socket = clientSocket;
		this.otherThreads = runnables;
	}
	
	//Returns username
	public String getName() {
		return this.user;
	}
	
	//Attempts to logon, and fails if username is already taken
	public boolean logon(String username) {
		for(int i = 0; i < otherThreads.size(); i++)
			synchronized (otherThreads) {
				if(username.equals(otherThreads.get(i).getName())) {
					addToQueue("That username is taken, please try again.");
					return false;
				}
			}
		this.user = username;
		Thread.currentThread().setName(user);
		announcement(user + " has logged on");
		return true;
	}
	
	//Makes a server-wide announcement
	public void announcement(String msg) {
		for(int i = 0; i < otherThreads.size(); i++)
			otherThreads.get(i).addToQueue("*" + msg + "*");
	}
	
	//Sends message to every user in the chat room
	public void shout(String msg) {
		for(int i = 0; i < otherThreads.size(); i++)
			synchronized (otherThreads) {
				if(this.user == otherThreads.get(i).getName())
					addToQueue("~You said: " + msg);
				else
					otherThreads.get(i).addToQueue(user + " says: " + msg);
			}
	}
	
	//Takes message and routes it to thread with name of destination
	public void privateMessage(String msg, String destination) {
		for(int i = 0; i < otherThreads.size(); i++)
			synchronized (otherThreads) {
				if(destination == otherThreads.get(i).getName()) {
					otherThreads.get(i).addToQueue(user + " sent you a message: " + msg);
					return;
				}
			}
		addToQueue(destination + " was not found!");
		return;
	}
	
	//adds message to this thread's message queue
	public void addToQueue(String msg) {
		queue.offer(msg);
	}
	
	//Begins by creating the the responder thread, then allows the user to log in and begins
	//monitoring for sent messages from the client
	public void run() {
		System.out.println("Connection with " + socket.getInetAddress() + " begun.");
		Responder responder = new Responder();
		Thread t = new Thread(responder);
		t.start();
		try {
			BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
			String inputLine;
			
			addToQueue("What is your username?");
			while(!logon(in.readLine()));
			
			while ((inputLine = in.readLine()) != null) {
				shout(inputLine);
			}
		}
		
		catch (IOException e) {
			System.err.println("Connection Lost");
			announcement(user + " has logged off");
			otherThreads.remove(this);
			Thread.currentThread().interrupt();
			return;
		}
	}
	
	//This runnable object is instantiated in this class's run, and
	//sits, waiting for a message to be added to the queue. When a message
	//IS found in the queue for this object, it sends the message back to the client.
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