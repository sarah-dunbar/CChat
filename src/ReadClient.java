import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;


//this class is the thread that will continue to listen 
//for any messages that other clients are sending
public class ReadClient extends Thread{

	ChatRoomGUI chatroom;
	Socket read;
	String[] tokens;
	String forward = "FRWD ";
	
	//we need to get the GUI that we will be updating as well as the socket
	//of the chat server we are receiving messages from
	public ReadClient(ChatRoomGUI gui, Socket sock) {
		chatroom = gui;
		read = sock;
	}
	
	//start when we start this thread in the client class
	public void run() {
		while(true) {
			InputStream stream = null;
			//try to read from the input stream
			try {
				stream = read.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Scanner scan = new Scanner(stream, "US-ASCII");
			String message;
			String prefix;
			
			//while the scanner has new line read through it
			while (scan.hasNextLine()) {
				//break up the message by the white space like our protocol specifies
				message = scan.nextLine();
				tokens = message.split("\\s+");
				prefix = tokens[0];
	    		
	    		//if we receive a forward message this means a client has sent a new message and we need 
				//to append this message to the GUI
	            if (prefix.equals("FWRD")) {
	            	String username = tokens[1];
	            	String printMessage = "";
	            	//this will allow us to iterate through the entire message since we have broken it up
	            	//by white space and placed in the tokens string array
	            	for(int i = 2; i< tokens.length; i++) {
	            		printMessage += tokens[i] + " ";
	            	}
	            	//call the method in GUI to add it to the display
	            	chatroom.addChat(username, printMessage);
	    		}
	            //if we receive a hello message  then we know a client has joined our chat room
	            //thus we will grab the user name of this client from the message and then call the 
	            //method in the GUI to display this event on the screen
	            else if(prefix.equals("HELO")) {
	            	String username = tokens[1];
	            	chatroom.addUser(username + " has joined the chat room.");
	            }
	            //if we receive a left message  then we know a client has left our chat room
	            //thus we will grab the user name of this client from the message and then call the 
	            //method in the GUI to display this event on the screen
	            else if(prefix.equals("LEFT")) {
	            	String username = tokens[2];
	            	chatroom.removeUser(username + " has left the chat room.");
	            }
	    	}
		}
	}
}