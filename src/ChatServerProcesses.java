import java.net.Socket;
import java.util.ArrayList;
import java.io.IOException;

//Separate thread for back-and-forth communication with the Group Router
public class ChatServerProcesses extends Thread{

	private Socket socket;
	private ChatServer chatServer;
	private volatile ArrayList<Socket> clientSocks;
	
	//Constructor
	public ChatServerProcesses(ChatServer currChatServer,Socket mySocket, ArrayList<Socket> myClientSocks){
		chatServer = currChatServer;
		socket = mySocket;
		clientSocks = myClientSocks;
	}
	
	//Method to update the ChatServerProcesses clientSocks variable, so that messages are not sent to clients with closed
	public void update(ArrayList<Socket> myClientSocks) {
		clientSocks = myClientSocks;
	}
	
	
	//Thread's run method, which infinitely reads and writes to the Group Router
	public void run(){
		try {
			while(true){
				//Reads from the Group Router
				String message = chatServer.read(socket);
				//If the message is a FWRD, LEFT, or HELO write it to every Client connected to the Chat Server
				if (message.substring(0, 4).equals("FWRD") || message.substring(0, 4).equals("LEFT") || message.substring(0, 4).equals("HELO")) {
					for(Socket clientSocket: clientSocks) {
						clientSocket.getOutputStream().write(message.getBytes("US-ASCII"),0,message.length());

	            	}
				}
				//If message is not FWRD, LEFT, or HELO write it to the Group Router
				else{
					chatServer.write(socket, message);
				}
			}
		} 
		//If thread could not start, write error to Chat Server
		catch (IOException e) {
			System.out.println("Something went wrong while threading...");
			e.printStackTrace();
		}
	}
}