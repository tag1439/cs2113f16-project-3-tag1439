package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Toni Giacchi
 */
public class ChatServer extends ChatWindow{

	private ClientHandler handler;
	ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
	//this arraylist contains all of the clients that are connected to the server

	public ChatServer() {
		super();
		this.setTitle("Chat Server");
		this.setLocation(80, 80);

		while (true) {//this while loop holds as long as there is a connection
			try {
				// Create a listening service for connections
				// at the designated port number.
				ServerSocket srv = new ServerSocket(2113);

				while (true) {
					// The method accept() blocks until a client connects.
					printMsg("Waiting for a connection");
					Socket socket = srv.accept();

					handler = new ClientHandler(socket);
					Thread t = new Thread(handler);//this creates a thread when a connection is created
					clients.add(handler);
					t.start();

				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}


	/** This innter class handles communication to/from one client. */
	class ClientHandler implements Runnable{
		private PrintWriter writer;
		private BufferedReader reader;


			public ClientHandler(Socket socket) throws IOException {
				try {
					InetAddress serverIP = socket.getInetAddress();
					printMsg("Connection made to " + serverIP);
					writer = new PrintWriter(socket.getOutputStream(), true);
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				}
				catch (IOException e){
						printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");

					}
			}
			public void handleConnection() {
				try {
					while(true) {
						// read a message from the client
						readMsg();
					}
				}
				catch (IOException e){
					printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
				}
			}

			/** Receive and display a message */
			public void readMsg() throws IOException {
				String s = reader.readLine();
				printMsg(s);//this reads in a message from the clinet and prints it to the server's screen
				sendMsg(s);//this then also sends the message back to all the clients so it appears on their screens also

			}
			/** Send a string */
			public void sendMsg(String s){
				for(int i = 0; i< clients.size(); i++){
					clients.get(i).writer.println(s);
					//this goes through the arraylist and sends a message to all of the clients in the chat
				}

			}

		@Override
		public void run() {
			handleConnection();//this has the thread handle the connection between the server and the clients

		}
	}


	public static void main(String args[]){
		new ChatServer();
	}
}
