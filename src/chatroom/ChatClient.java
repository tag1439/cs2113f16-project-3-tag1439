package chatroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Toni Giacchi
 */
public class ChatClient extends ChatWindow {

	// Inner class used for networking
	private Communicator comm;

	// GUI Objects
	private JTextField serverTxt;
	private JTextField nameTxt;
	private JButton connectB;
	private JTextField messageTxt;
	private JButton sendB;

	public ChatClient(){
		super();
		this.setTitle("Chat Client");
		printMsg("Chat Client Started.");

		// GUI elements at top of window
		// Need a Panel to store several buttons/text fields
		serverTxt = new JTextField("localhost");
		serverTxt.setColumns(15);
		nameTxt = new JTextField("Name");
		nameTxt.setColumns(10);
		connectB = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(serverTxt);
		topPanel.add(nameTxt);
		topPanel.add(connectB);
		contentPane.add(topPanel, BorderLayout.NORTH);

		// GUI elements and panel at bottom of window
		messageTxt = new JTextField("");
		messageTxt.setColumns(40);
		sendB = new JButton("Send");
		JPanel botPanel = new JPanel();
		botPanel.add(messageTxt);
		botPanel.add(sendB);
		contentPane.add(botPanel, BorderLayout.SOUTH);

		// Resize window to fit all GUI components
		this.pack();

		// Setup the communicator so it will handle the connect button
		Communicator comm = new Communicator();
		connectB.addActionListener(comm);
		sendB.addActionListener(comm);

	}

	/** This inner class handles communication with the server. */
	class Communicator implements ActionListener, Runnable{
		private Socket socket;
		private PrintWriter writer;
		private BufferedReader reader;
		private int port = 2113;

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			if(actionEvent.getActionCommand().compareTo("Connect") == 0) {
				connect();
				sendMsg(nameTxt.getText()+ " has joined.");
			}
			else if(actionEvent.getActionCommand().compareTo("Send") == 0) {
				String command = "/name";
				if(messageTxt.getText().length() > 5 && messageTxt.getText().substring(0,5).equals(command))
				{
					String newName = messageTxt.getText().substring(6);
					sendMsg(nameTxt.getText() + " has changed name to " + newName);
					nameTxt.setText(newName);
					//this if statement checks to see if the /name command is being used
					//if so, it will change the name of the person and show a message to the clients and server

				}
				else
					sendMsg(nameTxt.getText() + ": " + messageTxt.getText());
				//else it is going to send the message to the server and other clients
				//specifically indicating who sent it
			}

		}

		/** Connect to the remote server and setup input/output streams. */
		public void connect(){
			try {
				socket = new Socket(serverTxt.getText(), port);
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//creates a thread for each time a new client connects to the server
				Thread t = new Thread(this);
				t.start();
			}
			catch(IOException e) {
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}
		/** Receive and display a message */
		public void readMsg() throws IOException {
			try{
				String s = reader.readLine();
				printMsg(s);//reads the message from the server and prints it to the client's screen
			}
			catch(IOException e) {
				System.exit(0);
				//is there is nothing being read from server, it means the server closed
				//therefore we could also close the clients

			}

		}
		/** Send a string */
		public void sendMsg(String s){
			writer.println(s);//writes the message to the server

		}

		@Override
		public void run() {
			try {
				while(true) {
					readMsg();//when the threads starts, the client will begin to read in interactions with the server
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public static void main(String args[]){
		new ChatClient();
	}

}
