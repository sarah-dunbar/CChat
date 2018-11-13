import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

//ChatServer class for CChat
public class ChatServer extends Server {
	
	private String[] tokens;
	private String username;
	private Socket groupRouterSock;
	private InetAddress groupRouterAddress;
	private InetSocketAddress endpoint;
	private ChatServerProcesses toGroupRouter;
	private volatile ArrayList<Socket> clientSockets = new ArrayList<Socket>();
	private static final String NULL = "NULL \n";
	private static int csPort;

	// Make a new connection to a Group Router
	private void connectToGroupRouter(String grIP, int port) {
		try{
			// Setup the server side connection data to Group Router
			groupRouterAddress = InetAddress.getByName(grIP);
			endpoint = new InetSocketAddress(groupRouterAddress, port);

			//Make a TCP connection 
			groupRouterSock = new Socket();

			// Make the connection
			try {
				groupRouterSock.connect(endpoint);
			} catch(ConnectException e) {
				System.err.println("Cannot connect to server.");
				System.exit(1);
				return;
			}
			
			//Send PING message to give group router necessary info
			String ping = "PING " + csPort + " " + System.getProperty("line.separator");
			this.write(groupRouterSock, ping);
			this.read(groupRouterSock);
			
			//Start new thread the infinitely reads and writes from Group Router
			toGroupRouter = new ChatServerProcesses(this, groupRouterSock, clientSockets);
			toGroupRouter.start();
		}
		catch(Exception e) {
			System.err.println("Cannot connect to server.");
			System.exit(1);
			return;
		}
	}

	//Overrides Server's read method, telling what a read for a Chat Server should do
	@Override
	public String read(Socket readSock) throws UnsupportedEncodingException, IOException {
		
		//Grab and store Client sockets' info 
		if((!clientSockets.contains(readSock)) && readSock.getPort() != groupRouterSock.getPort()) {
			clientSockets.add(readSock);
			toGroupRouter.update(clientSockets);
		}
		
		//Check if other end has disconnected, if so close Chat Server side of connection
		if (!readSock.isConnected()) {
	    	  readSock.close();
		}
		
		//Gram newest message
		InputStream stream = readSock.getInputStream();
		Scanner scan = new Scanner(stream, "US-ASCII");
		String message;
		String prefix;
		while (scan.hasNextLine()) {
			message = scan.nextLine();
			tokens = message.split("\\s+");
			prefix = tokens[0];
    		
			//Forward HIII messages to Group Router
			if(prefix.equals("HIII")) {
    			message = "HELO " + tokens[1] + " " + System.getProperty("line.separator");
    			this.write(groupRouterSock, message);
    			return NULL;
			}
			//Return HELO messages to other end
			else if (prefix.equals("HELO")) {
				message += " " + System.getProperty("line.separator");
    			return message;
			}
			//Return FWRD messages to other end
			else if (prefix.equals("FWRD")) {
            	message += " " + System.getProperty("line.separator");
    			return message;
    		}
			//Send left messages to Group Router, update list of Client sockets to tell that Client has left.
			else if (prefix.equals("BYEE")) {
    			message = "LEFT " + tokens[1] + " " + tokens[2] + " " + System.getProperty("line.separator");
    			this.write(groupRouterSock, message);
    			readSock.close();
    			clientSockets.remove(readSock);
    			toGroupRouter.update(clientSockets);
    			return NULL;
			}
			//Return LEFT messages to other end
			else if (prefix.equals("LEFT")) {
            	message += " " + System.getProperty("line.separator");
    			return message;
    		}
			//Turn TEXT messages into FWRD messages and send to Group Router
            else if (prefix.equals("TEXT")) {
    			tokens = message.split(" ");
    			username = tokens[1];
    			message = "FWRD "+username+ " ";
    			for(int i = 2; i < tokens.length; i++) {
    				message+= tokens[i]+" ";
    			}
    			message+= "\n";
    			this.write(groupRouterSock, message);
    			return NULL;
    		}
			//Return NULL messages if nulls are received
            else if (prefix.equals("NULL")) {
    			return NULL;
    		}
			//If the message can't be understood, exit
            else{
            	System.err.println("Incorrect message received");
            	System.exit(1);
            }
            
    	}
		//Must return null so method works
		return NULL;
		
	}

	//Write method the overrides the Server class's write method
	@Override
	public void write(Socket writeSock, String message) throws IOException {
		//Write the message to the socket as long as the message is not a FWRD or NULL message
		if (!message.substring(0,4).equals("NULL") || !message.substring(0,4).equals("FWRD")) {
			writeSock.getOutputStream().write(message.getBytes("US-ASCII"),0,message.length());
		}
	}
	
	//Main that runs when ChatServer is started
	public static void main(String[] args) throws IOException {
		//Grab command line input (first argument is ChatServer's IP, second is ChatServer's port, third is Group Router's IP, and fourth one is Group Router's port)
		String csIP = args[0];
		csPort = Integer.parseInt(args[1]);
		String groupRouterIP = args[2];
		int grPort = Integer.parseInt(args[3]);
		ChatServer chatserver = new ChatServer();
		//Connect to the group router
		chatserver.connectToGroupRouter(groupRouterIP, grPort);
		//Infinitely read and write to Client
		chatserver.listenConnect(csIP, 10, csPort);
		
		
	}
}
