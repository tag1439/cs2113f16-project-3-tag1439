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

	public ChatServer() {
		super();
		this.setTitle("Chat Server");
		this.setLocation(80, 80);

		while (true) {
			try {
				// Create a listening service for connections
				// at the designated port number.
				ServerSocket srv = new ServerSocket(2113);

				while (true) {
					// The method accept() blocks until a client connects.
					printMsg("Waiting for a connection");
					Socket socket = srv.accept();

					handler = new ClientHandler(socket);
					Thread t = new Thread(handler);
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


			public ClientHandler(Socket socket) {
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
				printMsg(s);
				sendMsg(s);

			}
			/** Send a string */
			public void sendMsg(String s){
				for(int i = 0; i< clients.size(); i++){
					clients.get(i).writer.println(s);

				}

			}

		@Override
		public void run() {
			handleConnection();

		}
	}



	public static void main(String args[]){
		new ChatServer();
	}
}
