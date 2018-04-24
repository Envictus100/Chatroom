import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.*;

public class ServerThread implements Runnable {
	protected Socket socket;
	protected String user;
	protected String password;
	private ArrayList<ServerThread> otherThreads;
	protected BlockingQueue<String> queue = new ArrayBlockingQueue(10);
	
	//Constructor
	public ServerThread(Socket clientSocket, ArrayList<ServerThread> runnables, String password) {
		this.socket = clientSocket;
		this.otherThreads = runnables;
		this.password = password;
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
					addToQueue("/msg That username is taken, please try again.");
					return false;
				}
				else if(username.indexOf(' ') >= 0) {
					addToQueue("/msg That username has a space, please try again.");
					return false;
				}
			}
		this.user = username;
		Thread.currentThread().setName(user);
		announcement(user + " has logged on");
		return true;
	}
	
	//Attempts to logon, and fails if username is already taken
	public boolean password(String attempt) {
		if(attempt.equals(password))
			return true;
		addToQueue("/msg Wrong password IDIOT");
		return false;
	}
	
	//Makes a server-wide announcement
	public void announcement(String msg) {
		for(int i = 0; i < otherThreads.size(); i++)
			otherThreads.get(i).addToQueue("/msg *" + msg + "*");
	}
	
	//Sends message to every user in the chat room
	public void shout(String msg) {
		for(int i = 0; i < otherThreads.size(); i++)
			synchronized (otherThreads) {
				if(this.user == otherThreads.get(i).getName())
					addToQueue("/msg ~You said: " + msg);
				else
					otherThreads.get(i).addToQueue("/msg " + user + " says: " + msg);
			}
	}
	
	//Sends message to every user in the chat room
	public void shoutFile(String msg) {
		for(int i = 0; i < otherThreads.size(); i++)
			synchronized (otherThreads) {
				if(this.user == otherThreads.get(i).getName())
					addToQueue("/msg ~You sent a file");
				else
					otherThreads.get(i).addToQueue(msg);
			}
	}
	
	//Takes message and routes it to thread with name of destination
	public void privateMessage(String msg, String destination) {
		for(int i = 0; i < otherThreads.size(); i++)
			synchronized (otherThreads) {
				if(destination.equalsIgnoreCase(otherThreads.get(i).getName())) {
					otherThreads.get(i).addToQueue("/msg " + user + " sent you a message: " + msg);
					return;
				}
			}
		addToQueue("/msg " + destination + " was not found!");
		return;
	}
	
	//Takes message and routes it to thread with name of destination
	public void privateFile(String msg, String destination) {
		for(int i = 0; i < otherThreads.size(); i++)
			synchronized (otherThreads) {
				if(destination.equalsIgnoreCase(otherThreads.get(i).getName())) {
					otherThreads.get(i).addToQueue(msg);
					return;
				}
			}
		addToQueue("/msg " + destination + " was not found!");
		return;
	}
	
	//Takes message and routes it to thread with name of destination
	public void blacklistMessage(String msg, String destination) {
		boolean userFound = false;
		for(int i = 0; i < otherThreads.size(); i++)
			synchronized (otherThreads) {
				if(this.user == otherThreads.get(i).getName())
					addToQueue("/msg ~You said: " + msg);
				else if(!destination.equalsIgnoreCase(otherThreads.get(i).getName())) {
					otherThreads.get(i).addToQueue("/msg " + user + " sent you a message: " + msg);
				}
				else {
					userFound = true;
				}
			}
		if(!userFound)
			addToQueue("/msg " + destination + " was not found!");
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
			
			addToQueue("/msg What is the password?");
			while(!password(in.readLine()));
			addToQueue("/msg What is your username?");
			while(!logon(in.readLine()));
			addToQueue("/msg To send a server wide message, just type out your message and hit enter");
			addToQueue("/msg To send a direct message, type '/msg <username> <message>");
			addToQueue("/msg To send a server wide file, type '/file <filepath>");
			addToQueue("/msg To send a direct message file, type '/file /username <username> <filepath>");
			addToQueue("/msg To send a blacklisted message, type '/blk <username> <message>");
			
			while ((inputLine = in.readLine()) != null) {
				parseMessage(inputLine);
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
	
	public void parseMessage(String message) {
		String[] messageArray = message.split(" ");
		message = "";
		
		//Handles case if File is sent
		if(messageArray[0].equalsIgnoreCase("/file")) {
			
			//Handles direct file message
			if(messageArray[1].equalsIgnoreCase("/username")) {
				String strFileContents = ""; 
				try {
					FileInputStream fileInput = new FileInputStream(new File(messageArray[3]));
					BufferedInputStream Filecontents = new BufferedInputStream(fileInput);
					byte[] contents = new byte[2000000];

					int bytesRead = 0;
					while((bytesRead = Filecontents.read(contents)) != -1) { 
						strFileContents += new String(contents, 0, bytesRead);              
					}
					message = "/file " + messageArray[3] + " " + strFileContents;
				}
				catch(Exception e) {
					System.out.println(e);
				}
				privateFile(message, messageArray[2]);
			}
			
			//Handles file shout
			else {
				String strFileContents = ""; 
				try {
					FileInputStream fileInput = new FileInputStream(new File(messageArray[1]));
					BufferedInputStream Filecontents = new BufferedInputStream(fileInput);
					byte[] contents = new byte[2000000];

					int bytesRead = 0;
					while((bytesRead = Filecontents.read(contents)) != -1) { 
						strFileContents += new String(contents, 0, bytesRead);              
					}
					message = "/file " + messageArray[1] + " " + strFileContents;
				}
				catch(Exception e) {
					System.out.println(e);
				}
				shoutFile(message);
			}
		}
		
		//Handles case if msg is sent
		else if(messageArray[0].equalsIgnoreCase("/msg")) {
			for(int i = 2; i < messageArray.length; i++)
				message = message + messageArray[i] + " ";
			privateMessage(message, messageArray[1]);
		}
		
		//Handles case if blacklisted message is sent
		else if(messageArray[0].equalsIgnoreCase("/blk")) {
			for(int i = 2; i < messageArray.length; i++)
				message = message + messageArray[i] + " ";
			blacklistMessage(message, messageArray[1]);
		}
		
		//Handles all other cases
		else {
			for(int i = 0; i < messageArray.length; i++)
				message = message + messageArray[i] + " ";
			shout(message);
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