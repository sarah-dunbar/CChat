import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;


//this class is the thread that will continue to listen 
//for any messages that other clients are sending
public class WriteClient extends Thread{

	ChatRoomGUI chatroom;
	Socket write;
	String username;
	String text = "TEXT ";
	
	//the constructor will take in the GUI we need to edit and the socket
	//we are writing to as well as the user name we want to be identified as so we can 
	//send the proper information to the chat server
	public WriteClient(ChatRoomGUI gui, Socket sock, String username) {
		this.chatroom = gui;
		this.write = sock;
		this.username = username;
	}
	
	//start when we start this thread in the client class
	public void run() {
		while(true) {
			// constantly read in to see if the client as printed a message if it hasn't we just get that it is
			//null and it goes back through the loop
			String message = chatroom.getMessage();
			if (message == null) {
				
			}
			//if we get that message is not null then we have something that we need to write to the socket
			//we string the message in the format that has been specified by our protocol
			else {
				String finalmessage = text + username + " " + message + " " + System.getProperty("line.separator");
				byte[] rbuf = null;
				//place the message into the byte buffer 
				try {
					rbuf = finalmessage.getBytes("US-ASCII");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				//try to write the message to the client
				try {
					write.getOutputStream().write(rbuf, 0, rbuf.length);
					chatroom.editMessage();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
