import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;



//Class that will be used by clients to join the chat room
public class Client {
	
	private static String username;
	private static String groupname;
	private static String password;
	private static String initialize = "INIT ";
	private static String identification = "IDNT \n";
	private static ChatRoomGUI chatroom;
	private static volatile boolean done;
	private static volatile boolean left;


	//this method will open up the initial connection to the 
	//central server once we have retrieved the login information
	//from the user
	public static void openCentralServerSocket() throws IOException, UnsupportedEncodingException{
	    Socket sock;
	    InetAddress server_address;
	    InetSocketAddress endpoint;

	
	    // Setup the server side connection data
	    //hard coded in the port and IP of the central
	    //server as this will not change
	    server_address = InetAddress.getByName("172.27.40.222");
	    endpoint = new InetSocketAddress(server_address, 2020);
	    sock = new Socket();
	

	    // Make the connection with the central server
	    try {
	    	sock.connect(endpoint);
	    } catch(ConnectException e) {
	        System.err.println("Cannot connect to server.");
	        System.exit(1);
	        sock.close();
			return;
	    }
	    
	    //immediately send the message in the proper
	    //protocol to the central server
		String send = initialize + groupname + " " + password + " " + System.getProperty("line.separator");
		sock.getOutputStream().write(send.getBytes("US-ASCII"),0,send.length());
		
		
		//prepare to read in the message that contains the IP and port of the group router 
		//we are trying to connect to for the specific chat room we want to connect to
		//this message will be in our standard protocol format
		String[] tokens = null;		
		String message = null;
		
		InputStream stream = sock.getInputStream();
		Scanner scan = new Scanner(stream, "US-ASCII");
		
		while (scan.hasNextLine()) {
			message = scan.nextLine();
			//split up the message by white space as our protocol
			//specifies, store them in the tokens string array
			tokens = message.split("\\s+");
	    }
		//if we get an accept message from the central server then we know we inputed the correct password and 
		//group name thus we are going to receive the IP address and port
		if (tokens[0].equals("ACPT")) {
			String ipAddress = tokens[1];
			int port = Integer.parseInt(tokens[2]);
			//connect onto the group router by entering
			//the groupRouterSocket method
			groupRouterSocket(ipAddress, port);
		}
		//if we didn't input the correct information we will display a window to the user to 
		//let them know that this information was incorrect and allow them to try again.
		else if (tokens[0].equals("DENY")) {
			WrongInfo error = new WrongInfo("Group name  or Password was incorrect.");
		}
		//if anything else happens it means something is wrong with the central
		//server and we need to close out of the program all together
		else {
			System.err.println("There was an issue connecting with the central server.");
			System.exit(1);
		}	
		//close this socket after it has fulfilled its purpose
		sock.close();
    }
	
	
	//this method is going to connect us to the group router, the next step into getting us into a chat room
	//this method takes in the IP and port of the group router we are trying to connect to
	public static void groupRouterSocket(String ipAddress, int port) throws IOException {
		//creates the chat room that the user will see on the screen
		//creates it immediately so the user feels that they are part of the
		//chat room almost automatically after joining
		chatroom = new ChatRoomGUI(groupname, left);

		Socket sock;
	    InetAddress server_address;
	    InetSocketAddress endpoint;

	    // Setup the server side connection data
	    server_address = InetAddress.getByName(ipAddress);
	    endpoint = new InetSocketAddress(server_address, port);
	    sock = new Socket();
	
	    // Make the connection with the group router
	    try {
	    	sock.connect(endpoint);
	    } catch(ConnectException e) {
	        System.err.println("Cannot connect to server.");
	        System.exit(1);
			return;
	    }
	    //get the output of the group router
	    //we immediately expect the group router to give us the ip and port of the chat server it 
	    //has decided that we will connect to
		sock.getOutputStream().write(identification.getBytes("US-ASCII"),0,identification.length());
		
		
		String[] tokens = null;		
		String message = null;
		InputStream stream = sock.getInputStream();
		Scanner scan = new Scanner(stream, "US-ASCII");
		while (scan.hasNextLine()) {
			message = scan.nextLine();
			tokens = message.split("\\s+");
			//if the message is an accept message it means that the group router has understood that
			//we are a client trying to connect to a chat server to begin a chat room
			if (tokens[0].equals("ACPT")) {
				//from here we grab the IP and port of the chat server we have been told to connect to
				String finalIpAddress = tokens[1];
				int finalPort = Integer.parseInt(tokens[2]);
				//head to the method chatServerSock which will allow us to make our final connection
				chatServerSock(finalIpAddress, finalPort);
			}
			//if the group router denies us access than we let the client know that we were
			//not able to connect us to any chat rooms and they need to try to connect again
			else if (tokens[0].equals("DENY")) {
				WrongInfo error = new WrongInfo("Cannot connect you to the chat room.");
			}
			//if anything else happens it means something is wrong with the group router
			//and we need to close out of the program all together and reconnect
			else {
				System.err.println("We have lost connection with the group router.");
				System.exit(1);
			}	
			//close the socket
			sock.close();
	    }
		

	    
	}
	
	//this method is going to take in the IP address and port of the chat server that we are trying to
	//connect to
	public static void chatServerSock(String ipAddress, int port) throws IOException {

		Socket sock;
	    InetAddress server_address;
	    InetSocketAddress endpoint;

	
	    // Setup the server side connection data
	    server_address = InetAddress.getByName(ipAddress);
	    endpoint = new InetSocketAddress(server_address, port);
	    sock = new Socket();


	    // Make the connection with the Chat Server
	    try {
	    	sock.connect(endpoint);
	    } catch(ConnectException e) {
	        System.err.println("Cannot connect to server.");
	        System.exit(1);
			return;
	    }		
	    
	    //send the initial hello message to the Chat server so they 
	    //can let everyone know this specific client has joined the chat room
	    String helloMessage = "HIII " + username + " \n";
	    sock.getOutputStream().write(helloMessage.getBytes("US-ASCII"), 0, helloMessage.length());
	    
	    //begin to run two threads, one will write anything to chat server socket
	    //that the client has written in the text box, while the other will be reading in any messages
	    //other people have sent in the group chat and display them for the user to see
	    ReadClient read = new ReadClient(chatroom, sock);
	    WriteClient write = new WriteClient(chatroom, sock, username);
	    //start the threads
	    read.start();
	    write.start();
	    //what to see if we have closed out of the chat and left the chat
	    while(chatroom.left() == false) {
	    	
	    }
	    
	    //loop will break once we have left the chat and thus we can now send a LEFT messgae to the 
	    //chat server to notify all the other clients that we have left the chat
	    String leftMessage = "BYEE " + server_address.getHostAddress() + " " + username + " \n";
	    sock.getOutputStream().write(leftMessage.getBytes("US-ASCII"), 0, leftMessage.length());
	    //now we can exit the system
	    System.exit(0);
	}
	
	



	//main method
	public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException, IOException {
		//creates two booleans that will be used to have out threads to wait till others have been executed
		done = false;
		left = false;
		//create the GUI, and pass in the done boolean
		Welcome welcome = new Welcome(done);
		
		//the main method will come to a halt till the welcome changes the done
		//boolean to true which will happen once the client has inputed the information
		while(welcome.done() == false) {		
		}

		//get the information from the GUI
		//store it to be used to send to the Central Server
		String[] clientInfo = welcome.getInfo();
		username = clientInfo[0];
		groupname = clientInfo[1];
		password = clientInfo[2];
		
		//begin the connection with the central server
		openCentralServerSocket();
	}
}
